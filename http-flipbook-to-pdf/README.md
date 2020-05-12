
# Running Flipbook2Pdf


Compile it:

```
mvn package
```

Configure your email+passwd in your home configuration file "linuxmag.properties":
(on linux: in your $HOME: "/home/${user}/linuxmag.properties", on windows in "c:\\users\\${user}\\linuxmag.properties" ) 

```
email=<<youremail>>
passwd=<<yourpassword>>
```

Run it:

- either from your IDE (Eclipse, IntelliJ): simply run the main class fr.an.tools.flipbook2pdf.Flipbook2PdfMain

- or from command line

```
java -jar target/flipbook2pdf-0.0.1-SNAPSHOT.jar
```

By default, it will download all your flipbooks... and cache result in local directory "out" 

Read your pdf offline: open local directory "out"

        