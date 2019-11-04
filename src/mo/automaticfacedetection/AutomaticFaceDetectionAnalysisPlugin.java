
package mo.automaticfacedetection;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import mo.analysis.AnalysisProvider;
import static mo.analysis.NotesAnalysisPlugin.logger;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;


@Extension(
    xtends = {
            @Extends(
                    extensionPointId = "mo.analysis.AnalysisProvider"
                    )
             }
            )


public class AutomaticFaceDetectionAnalysisPlugin implements AnalysisProvider    {

    List<Configuration> configs;

    public AutomaticFaceDetectionAnalysisPlugin() {
        this.configs =  new ArrayList<Configuration>();
    }
 
    @Override
    public String getName() {
        return "Automatic Face Detection Analysis";
    }

    @Override
    public Configuration initNewConfiguration(ProjectOrganization organization) {

        AutomaticFaceDetectionAnalysisConfigDialog d = new AutomaticFaceDetectionAnalysisConfigDialog();
        boolean accepted = d.showDialog();

        
        if (accepted) {
            AutomaticFaceDetectionAnalysisConfig c1 = new AutomaticFaceDetectionAnalysisConfig(d.getConfigurationName());
            configs.add(c1);     
            
            return c1;
        }
        
        return null;        

    }

    @Override
    public List<Configuration> getConfigurations() {
        return this.configs;
    }

    @Override
    public StagePlugin fromFile(File file) {
        if (file.isFile()) {
            try {
                AutomaticFaceDetectionAnalysisPlugin mc = new AutomaticFaceDetectionAnalysisPlugin();
                XElement root = XIO.readUTF(new FileInputStream(file));
                XElement[] pathsX = root.getElements("path");
                for (XElement pathX : pathsX) {
                    String path = pathX.getString();
                    AutomaticFaceDetectionAnalysisConfig c = new AutomaticFaceDetectionAnalysisConfig();
                    Configuration config = c.fromFile(new File(file.getParentFile(), path));
                    if (config != null) {
                        mc.configs.add(config);
                    }
                }
                return mc;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public File toFile(File parent) {
        File file = new File(parent, "automatic-face-detection-analysis.xml");
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        XElement root = new XElement("analysis");
        
        File p = new File(parent, "automatic-face-detection-analysis");
        p.mkdirs();        
        
        for (Configuration config : configs) {

            File f = config.toFile(p);

            XElement path = new XElement("path");
            Path parentPath = parent.toPath();
            Path configPath = f.toPath();
            path.setString(parentPath.relativize(configPath).toString());
            root.addElement(path);
        }
        try {
            XIO.writeUTF(root, new FileOutputStream(file));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return file;
        
        
    }
 
    
    
    
}
