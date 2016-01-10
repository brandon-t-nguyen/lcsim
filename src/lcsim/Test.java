package lcsim;

import java.awt.Color;

import java.io.*;
import java.util.zip.*;
import java.net.*;

import lcsim.gui.*;
import lcsim.pkg.*;
import lcsim.pkg.gui.*;
import lcsim.pkg.Package;
import lcsimlib.*;

public class Test
{
    public static void testGUI(LCSystem sys)
    {
        PackageManager pacman = new PackageManager(sys,"packages/");
        pacman.runLoadscript("lc3.lds");
        MainFrame main = new MainFrame(sys,pacman);
        main.setVisible(true);
        
        while(!sys.isCoreLoaded())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        boolean firstStop = true;
        sys.stopRunning();
        sys.enableProfiling();
        while(true)
        {
            if(sys.isRunning())
            {
                if(firstStop)
                {
                    firstStop = false;
                }
                sys.profileCycle();
            }
            else
            {
                if(!firstStop)
                {
                    break;
                }
                else
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        sys.printProfile();
    }
    
    public static void testLoadScript(LCSystem sys)
    {
        PackageManager pacman = new PackageManager(sys,"packages/");
        pacman.runLoadscript("lc3.lds");
        PackageManagerWindow pacmanGui= new PackageManagerWindow(pacman);
        pacmanGui.setVisible(true);
        
        System.out.println("Successfully loaded script");
        
        if(sys.load("termout.obj"))
        {
            System.out.println("Successfully loaded termout.obj");
        }
        else
        {
            System.out.println("Unsuccessful code load");
        }
        sys.enableProfiling();
        while(sys.isRunning())
        {
            sys.profileCycle();
        }
        sys.printProfile();
        System.exit(0);
    }
    public static void testPackageManagerGui(LCSystem sys)
    {
        PackageManager pacman = new PackageManager(sys,"packages/");
        PackageManagerWindow pacmanGui= new PackageManagerWindow(pacman);
        pacmanGui.setVisible(true);
        while(pacmanGui.isShowing())
        {
        }
        sys.load("termout.obj");
        sys.enableProfiling();
        while(sys.isRunning())
        {
            sys.profileCycle();
        }
        sys.printProfile();
        System.exit(0);
    }
    
    public static void testPackageManagerTerm(LCSystem sys)
    {
        PackageManager pacman = new PackageManager(sys,"packages/");
        CorePackage[] cores = pacman.getCores();
        DevicePackage[] devices = pacman.getDevices();
        CodeLoaderPackage[] loaders = pacman.getCodeLoaders();
        sys.setCore(cores[0].createObject());
        sys.addDevice(devices[0].createObject());
        sys.addLoader(loaders[0].createObject());
        sys.load("termout.obj");
        
        sys.enableProfiling();
        while(sys.isRunning())
        {
            sys.profileCycle();
        }
        sys.printProfile();
        System.exit(0);
    }
    
    public static void testPackageManager(LCSystem sys)
    {
        PackageManager pacman = new PackageManager(sys,"packages/");
    }

    public static void testPackage(LCSystem sys)
    {
        //Hardcoded direct loading of packages
        File coreFile = new File("packages/lc3standard.pkg");
        Package corepkg = new Package(coreFile);
        Core core = (Core) corepkg.createObject();
        
        File consoleFile = new File("packages/lc3console.pkg");
        Package consolepkg = new Package(consoleFile);
        Device console = (Device) consolepkg.createObject();
        
        File objFile = new File("packages/lc3objloader.pkg");
        Package objpkg = new Package(objFile);
        CodeLoader objLoader = (CodeLoader) objpkg.createObject();
        
        //Add them to the system
        sys.setCore(core);
        sys.addDevice(console);
        sys.addLoader(objLoader);
        sys.load("termout.obj");
        sys.enableProfiling();
        while(sys.isRunning())
        {
            sys.profileCycle();
        }
        sys.printProfile();

        System.exit(0);
        
    }
    
    public static void testPackageDirect(LCSystem sys)
    {
        
        //Hardcoded loading core
        try
        {
            File file = new File("packages/lc3standard.pkg");
            InputStream stream;
            ZipFile jar = new ZipFile(file);
            ZipEntry entry = jar.getEntry("meta.txt");
            System.out.println(entry);
            stream = jar.getInputStream(entry);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String className = reader.readLine();
            System.out.println(className);
            URLClassLoader URLloader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Core core = (Core) URLloader.loadClass(className).newInstance();
           
            sys.setCore(core);
            
            //Hardcoded loading console
            file = new File("packages/lc3console.pkg");
            jar = new ZipFile(file);
            entry = jar.getEntry("meta.txt");
            System.out.println(entry);
            stream = jar.getInputStream(entry);
            reader = new BufferedReader(new InputStreamReader(stream));
            className = reader.readLine();
            System.out.println(className);
            URLloader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Device console = (Device) URLloader.loadClass(className).newInstance();
            
            sys.addDevice(console);
            
            
            //Hardcoded codeloader
            file = new File("packages/lc3objloader.pkg");
            jar = new ZipFile(file);
            entry = jar.getEntry("meta.txt");
            System.out.println(entry);
            stream = jar.getInputStream(entry);
            reader = new BufferedReader(new InputStreamReader(stream));
            className = reader.readLine();
            System.out.println(className);
            URLloader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            CodeLoader objloader = (CodeLoader) URLloader.loadClass(className).newInstance();
            objloader.init(sys);
            
            objloader.load("termout.obj");
            
            sys.enableProfiling();
            while(sys.isRunning())
            {
                sys.profileCycle();
            }
            sys.printProfile();
            System.exit(0);
        }
        catch(Exception e)
        {
        }
    }
    
}
