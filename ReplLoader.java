import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.io.File;

class ReplLoader {
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
        //not needed
        //ClassLoader pcl = ClassLoader.getSystemClassLoader();
        URL[] cpUrls;
        if (args.length < 1) {
            System.out.println("usage: ReplLoader <Class (FQ)> [classpath]");
            return;
        } else if (args.length < 2) {
            cpUrls = new URL[] {};
        } else {
            cpUrls = Arrays.stream(args[1].split(File.pathSeparator, 0)).<URL>map(s -> {    
                try {
                    return new File(s).toURI().toURL();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }).toArray(URL[]::new);
        }
        //System.out.println("CP-URL: "+cpUrls[0]);
        //System.out.println("kc.lib.dir: "+System.getProperty("kc.lib.dir"));
        URLClassLoader ucl = new URLClassLoader(cpUrls, null);
        final Class<?> cls = ucl.loadClass(args[0]);
        final Method method = cls.getMethod("main", String[].class);
        final Object[] argx = new Object[1];
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(isr);
        String inpL;
        while(!(inpL = reader.readLine()).equals("quit")) {
            argx[0] = inpL.split(" ", 0);
            method.invoke(null, argx);
        }
        System.out.println("received quit, exiting...");
    }
}
