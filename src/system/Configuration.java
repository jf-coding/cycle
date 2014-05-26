package system;

/**
 * The defaults system configuration class.
 */
public class Configuration 
{
 /** The program name. */
 public static final String program_name   = "Cycle";
 /** The command line name. */
 public static final String command        = "cycle";
 /** The version date. */
 public static final String date           = "26/05/2014";
 /** The version number. */
 public static final String version        = "0.0.0.1";
 /** The author name. */
 public static final String name           = "Joao Ferreira";
 /** The author email. */
 public static final String email          = "joao.dos.santos@gmail.com";
 /** The Development Tools folder full path name. */
 public static final String root_folder    = System.getProperty("java.class.path") + (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == 0 ? "\\" : "/");
 /** The Development Tools folder name. */
 public static final String program_folder = "dtool";
 /** The Development Tools configuration folder name. */
 public static final String configuration  = "configuration_files";
 /** The Development Tools processors folder name. */
 public static final String processors     = "processors";
 /** The Development Tools devices folder name. */
 public static final String devices        = "opbdevices";
 /** The default system configuration file name. */
 public static final String systemconfig_file_default = "systemconfig.xml";
 /** The default profile file name. */
 public static final String profile_file_default      = "profile.txt";
 /** The default trace file name. */
 public static final String trace_file_default        = "trace.txt";
 /** The default port number. */
 public static final int    port_number_default       = 1234;
 /** The configuration folder full path name. */
 public static final String configuration_folder = root_folder + configuration + (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == 0 ? "\\" : "/");
 /** The default system configuration file full path name. */
 public static final String systemconfig_file_default_full_path = configuration_folder + systemconfig_file_default;
}
