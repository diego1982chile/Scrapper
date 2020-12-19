package cl.dsoto.helpers;

import cl.dsoto.model.FileControl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ProcessHelper {

    private static final ProcessHelper instance = new ProcessHelper();

    /** La lista de tagSMTK */
    private List<FileControl> fileControlList;

    /** Mapa de tagSMTK por su nombre. */
    private ConcurrentHashMap<String, FileControl> fileControlMap;

    public List<FileControl> getFileControlList() {
        return fileControlList;
    }

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ProcessHelper() {
        this.fileControlList = new ArrayList<FileControl>();
        this.fileControlMap = new ConcurrentHashMap<String,FileControl>();
    }

    public static ProcessHelper getInstance() {
        return instance;
    }

    /**
     * Este método es responsable de retornar el tipo de descripción llamado FSN.
     *
     * @return Retorna una instancia de FSN.
     */
    public FileControl findFileControlByName(String name) {

        if (fileControlMap.containsKey(name)) {
            return this.fileControlMap.get(name);
        }

        return null;
    }

    /**
     * Este método es responsable de asignar un nuevo conjunto de tagsSMTJ. Al hacerlo, es necesario actualizar
     * los mapas.
     */
    public void registerFileControl(FileControl fileControl) {
        /* Se actualiza el mapa por nombres */
        fileControlMap.put(fileControl.getFileName(), fileControl);
    }


}
