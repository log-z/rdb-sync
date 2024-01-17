package vip.logz.rdbsync.common.utils;

import vip.logz.rdbsync.common.annotations.Scannable;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类型扫描器
 *
 * @author logz
 * @date 2024-01-10
 */
public class ClassScanner {

    /** 文件路径分隔符 */
    private static final String FILEPATH_DELIMITER = "/";

    /** 包路径分隔符 */
    private static final String PACKAGE_DELIMITER = ".";

    /** URL协议：文件系统 */
    private static final String URL_PROTOCOL_FILE = "file";

    /** URL协议：JAR */
    private static final String URL_PROTOCOL_JAR = "jar";

    /** 文件后缀：Java字节码文件 */
    private static final String FILE_SUFFIX_CLASS = ".class";

    /** 基础包路径 */
    private final String basePackage;

    /** 是否深度扫描（递归） */
    private final boolean recursive;

    /** 包路径鉴定器 */
    private final Predicate<String> packagePredicate;

    /** 类型鉴定器 */
    private final Predicate<Class<?>> classPredicate;

    /**
     * 构造器
     * @param basePackage 基础包路径
     * @param recursive 是否深度扫描（递归）
     * @param packagePredicate 包路径鉴定器（为null表示都满足）
     * @param classPredicate 类型鉴定器（为null表示都满足）
     */
    public ClassScanner(String basePackage,
                        boolean recursive,
                        Predicate<String> packagePredicate,
                        Predicate<Class<?>> classPredicate) {
        this.basePackage = basePackage;
        this.recursive = recursive;
        this.packagePredicate = packagePredicate;
        this.classPredicate = classPredicate;
    }

    /**
     * 扫描类型
     * @return 返回已扫描到的类型集合
     * @throws IOException 资源访问出错时抛出此异常
     */
    public Set<Class<?>> doScan() throws IOException {
        // 类型收集容器
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 包路径
        String packagePath = StringUtils.removeEnd(basePackage, PACKAGE_DELIMITER);
        // 包路径的文件表示
        String basePackageFilePath = packagePath.replace(PACKAGE_DELIMITER, FILEPATH_DELIMITER);

        // 获取所有资源URL，并遍历
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(basePackageFilePath);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();

            try {
                if (URL_PROTOCOL_FILE.equals(protocol)) {
                    // 展开资源：当位于文件系统
                    String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
                    doScanByFile(classes, packagePath, filePath);
                } else if (URL_PROTOCOL_JAR.equals(protocol)) {
                    // 展开资源：当位于JAR包
                    doScanByJar(classes, packagePath, resource);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return classes;
    }

    /**
     * 在文件系统中扫描指定包的类型
     * @param classes 类型收集容器
     * @param packagePath 包路径
     * @param filePath 文件路径
     * @throws ClassNotFoundException 代码逻辑有误时可能抛出此异常
     */
    private void doScanByFile(Set<Class<?>> classes, String packagePath, String filePath)
    throws ClassNotFoundException {
        // 情形1：路径不存在或不是目录
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;  // 提前退出
        }

        // 情形2：路径是有效目录
        // 列出当前目录的 .class 文件，如需深度扫描则包含子目录
        File[] files = dir.listFiles(file -> {
            String fileName = file.getName();
            if (!file.isDirectory()) {
                return fileName.endsWith(FILE_SUFFIX_CLASS);
            }

            if (!recursive) {
                return false;
            }

            // 包路径鉴定
            if (packagePredicate != null) {
                return packagePredicate.test(packagePath + PACKAGE_DELIMITER + fileName);
            }

            return true;
        });

        if (files == null) {
            return;
        }

        // 处理这些文件或子目录
        for (File file : files) {
            String fileName = file.getName();

            // 情形2.1：递归处理子目录
            if (file.isDirectory()) {
                String subPackageName = packagePath + PACKAGE_DELIMITER + fileName;
                doScanByFile(classes, subPackageName, file.getAbsolutePath());
                continue;
            }

            // 情形2.2：加载 .class 文件并收集
            String className = StringUtils.removeEnd(fileName, FILE_SUFFIX_CLASS);
            loadAndCollectClass(classes, packagePath + PACKAGE_DELIMITER + className);
        }
    }

    /**
     * 在JAR中扫描指定包的类型
     * @param classes 类型收集容器
     * @param packagePath 包路径
     * @param url URL资源
     * @throws IOException 资源访问出错时抛出此异常
     * @throws ClassNotFoundException 代码逻辑有误时可能抛出此异常
     */
    private void doScanByJar(Set<Class<?>> classes, String packagePath, URL url)
    throws IOException, ClassNotFoundException {
        // 包路径的文件表示
        String basePackageFilePath = packagePath.replace(PACKAGE_DELIMITER, FILEPATH_DELIMITER);

        // 打开JAR文件
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        // 遍历JAR内部条目
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();

            // 情形1：不在指定的包中，或这是个目录
            if (!name.startsWith(basePackageFilePath) || jarEntry.isDirectory()) {
                continue;  // 忽略它
            }

            // 情形2：禁用深度扫描时，却位于更深层的包中
            int endIndex = name.lastIndexOf(FILEPATH_DELIMITER);
            if (!recursive && endIndex != basePackageFilePath.length()) {
                continue;  // 忽略它
            }

            // 情形3：包路径鉴定不通过
            if (packagePredicate != null) {
                String jarPackageName = name.substring(0, endIndex)
                        .replace(FILEPATH_DELIMITER, PACKAGE_DELIMITER);
                if (!packagePredicate.test(jarPackageName)) {
                    continue;  // 忽略它
                }
            }

            // 情形4：这是个有效的 .class 条目
            // 加载 .class 条目并收集
            String className = StringUtils.removeEnd(name, FILE_SUFFIX_CLASS)
                    .replace(FILEPATH_DELIMITER, PACKAGE_DELIMITER);
            loadAndCollectClass(classes, className);
        }
    }

    /**
     * 加载并收集类型
     * @param classes 类型收集容器
     * @param className 类名
     * @throws ClassNotFoundException 类加载失败时抛出此异常
     */
    private void loadAndCollectClass(Set<Class<?>> classes, String className) throws ClassNotFoundException {
        // 加载类型
        Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(className);
        // 鉴定类型满足条件后收集
        if (cls.getAnnotation(Scannable.class) != null) {
            if (classPredicate == null || classPredicate.test(cls)) {
                classes.add(cls);
            }
        }
    }

    /**
     * 快捷方式：按超类扫描
     * @param superclass 超类
     * @param basePackage 基础包路径
     * @return 返回继承或实现了超类的所有类型
     * @param <T> 超类
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Set<Class<? extends T>> scanByClass(Class<T> superclass, String basePackage) {
        Predicate<Class<?>> classPredicate = cls -> (cls != superclass) && superclass.isAssignableFrom(cls);
        ClassScanner classScanner = new ClassScanner(basePackage, true, null, classPredicate);

        try {
            return (Set) classScanner.doScan();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 快捷方式：按注解扫描
     * @param annotation 注解
     * @param basePackage 基础包路径
     * @return 返回使用了此注解的所有类型
     */
    public static Set<Class<?>> scanByAnnotation(Class<? extends Annotation> annotation, String basePackage) {
        Predicate<Class<?>> classPredicate = cls -> cls.getAnnotation(annotation) != null;
        ClassScanner classScanner = new ClassScanner(basePackage, true, null, classPredicate);

        try {
            return classScanner.doScan();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
