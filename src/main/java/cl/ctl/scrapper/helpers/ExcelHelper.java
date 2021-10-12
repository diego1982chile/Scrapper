package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.scraps.AbstractRegister;
import cl.ctl.scrapper.model.scraps.SMURecord;

import java.awt.print.Book;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 17-12-20.
 */
public class ExcelHelper {


    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(ExcelHelper.class.getName());

    static LogHelper fh = LogHelper.getInstance();

    private static final ExcelHelper instance = new ExcelHelper();

    public static ExcelHelper getInstance() {
        return instance;
    }


    public ExcelHelper() {

    }

    /*
    public List<SMURecord> readSMURecordsFromExcelFile(String excelFilePath) throws IOException {

        List<SMURecord> smuRecords = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            SMURecord smuRecord = new SMURecord();

            while (cellIterator.hasNext()) {
                Cell nextCell = cellIterator.next();
                int columnIndex = nextCell.getColumnIndex();

                switch (columnIndex) {
                    case 1:
                        smuRecord.setPeriodo(nextCell.getStringCellValue());
                        break;
                    case 2:
                        smuRecord.setCodUnimcarc(nextCell.getStringCellValue());
                        break;
                    case 3:
                        smuRecord.setCodProveedor(nextCell.getStringCellValue());
                        break;
                    case 4:
                        smuRecord.setDescripcionProducto(nextCell.getStringCellValue());
                        break;
                    case 5:
                        smuRecord.setEstadoProducto(nextCell.getStringCellValue());
                        break;
                    case 6:
                        smuRecord.setUnidadMedidaBase(nextCell.getStringCellValue());
                        break;
                    case 7:
                        smuRecord.setCodLocal(nextCell.getStringCellValue());
                        break;
                    case 8:
                        smuRecord.setCodLocal(nextCell.getStringCellValue());
                        break;
                    case 10:
                        smuRecord.setDescripcionLocal(nextCell.getStringCellValue());
                        break;
                    case 11:
                        smuRecord.setEstadoLocal(nextCell.getStringCellValue());
                        break;
                    case 12:
                        smuRecord.setFormato(nextCell.getStringCellValue());
                        break;
                    case 13:
                        smuRecord.setTipo(nextCell.getStringCellValue());
                        break;
                    case 14:
                        smuRecord.setVtaUnid(nextCell.getNumericCellValue());
                        break;
                    case 15:
                        smuRecord.setVtaPub(nextCell.getNumericCellValue());
                        break;
                    case 16:
                        smuRecord.setVtaCosto(nextCell.getNumericCellValue());
                        break;
                    case 17:
                        smuRecord.setInventario(nextCell.getNumericCellValue());
                        break;
                    case 18:
                        smuRecord.setInvACosto(nextCell.getNumericCellValue());
                        break;
                }

            }
            smuRecords.add(smuRecord);
        }

        workbook.close();
        inputStream.close();

        return smuRecords;
    }
    */

}
