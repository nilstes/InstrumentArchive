package services;

import data.Instrument;
import data.InstrumentFilter;
import data.Musician;
import data.Status;
import db.InstrumentDao;
import db.MusicianDao;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author nilstes
 */
@Path("excel")
public class ExcelService extends SecureService {

    private static final Logger log = Logger.getLogger(InstrumentService.class.getName());

    private InstrumentDao instrumentDao = new InstrumentDao();
    private MusicianDao musicianDao = new MusicianDao();

    @GET
    @Produces("application/vnd.ms-excel")
    public Response getWorkbook() {
        checkLogon();

        try {
            log.info("Creating spreadsheet...");
            final HSSFWorkbook workbook = new HSSFWorkbook();

            log.info("Creating lent instruments tab...");
            HSSFSheet lentInstrumentsSheet = workbook.createSheet("Utlånt");
            List<Instrument> instruments = instrumentDao.getInstruments(InstrumentFilter.LENT);
            createInstrumentList(workbook, instruments, lentInstrumentsSheet);

            log.info("Creating available instruments tab...");
            HSSFSheet availableInstrumentsSheet = workbook.createSheet("Tilgjengelig");
            instruments = instrumentDao.getInstruments(InstrumentFilter.AVAILABLE);
            createInstrumentList(workbook, instruments, availableInstrumentsSheet);

            log.info("Creating lent musicians tab...");
            HSSFSheet musiciansSheet = workbook.createSheet("Musikere");
            List<Musician> musicians = musicianDao.getMusicians();
            createMusiciansList(workbook, musiciansSheet, musicians);

            log.info("Streaming spreadsheet to client...");
            return Response.ok(new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                     workbook.write(output);
                }
            }).header("Content-Disposition",  "attachment; filename=\"instrumenter.xls\"").build();
        } catch(SQLException e) {
            log.log(Level.SEVERE, "Failed to create spreadsheet", e);        
            throw new ServerErrorException("Failed to create spreadsheet", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    private void createMusiciansList(final HSSFWorkbook workbook, HSSFSheet sheet, List<Musician> musicians) {
        List<String> instruments;
        createHeaders(workbook, sheet, Arrays.asList("Navn", "Instrumenter"));
        for(int i=0; i<musicians.size(); i++) {
            HSSFRow row = sheet.createRow(i+1);
            createTextCell(row, musicians.get(i).getLastName() + ", " + musicians.get(i).getFirstName(), 0);
            instruments = musicians.get(i).getInstruments();
            createTextCell(row, createInstrumentListString(instruments), 1);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void createInstrumentList(HSSFWorkbook workbook, List<Instrument> instruments, HSSFSheet sheet) {
        createHeaders(workbook, sheet, Arrays.asList("Type", "Merke", "Produktnummer", "Serienummer", "Beskrivelse", "Utlånt til", "Siste status", "Dato status"));
        for(int i=0; i<instruments.size(); i++) {
            HSSFRow row = sheet.createRow(i+1);
            List<Status> statuses = instruments.get(i).getStatuses();
            Status lastStatus = statuses.size()==0?null:statuses.get(statuses.size()-1);
            String lentTo = instruments.get(i).getLentTo();
            createTextCell(row, instruments.get(i).getType(), 0);
            createTextCell(row, instruments.get(i).getMake(), 1);
            createTextCell(row, instruments.get(i).getProductNo(), 2);
            createTextCell(row, instruments.get(i).getSerialNo(), 3);
            createTextCell(row, instruments.get(i).getDescription(), 4);
            createTextCell(row, lentTo==null?"":lentTo, 5);
            createTextCell(row, lastStatus==null?"":lastStatus.getText(), 6);
            createTextCell(row, lastStatus==null?"":lastStatus.getDate().toString(), 7);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);        
        sheet.autoSizeColumn(7);
    }

    private void createTextCell(HSSFRow row, String text, int index) {
        HSSFCell cell = row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(text);
    }
   
    private void createHeaders(HSSFWorkbook workbook, HSSFSheet sheet, List<String> headers) {
        HSSFRow row = sheet.createRow(0);
        for(int i=0; i<headers.size(); i++) {
            createHeaderCell(workbook, row, headers.get(i), i);           
        }
    }
    
    private HSSFCell createHeaderCell(HSSFWorkbook workbook, HSSFRow row, String header, int index) {
        HSSFCell cell = row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(header);
        CellStyle style = workbook.createCellStyle();
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setFontHeightInPoints((short) 10);
        hSSFFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        hSSFFont.setUnderline(HSSFFont.U_SINGLE);
        hSSFFont.setColor(HSSFColor.BLUE.index);
        style.setFont(hSSFFont);
        cell.setCellStyle(style);
        return cell;
    }

    private String createInstrumentListString(List<String> instruments) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<instruments.size(); i++) {
            sb.append(instruments.get(i));
            if(i < instruments.size() - 1) {
                sb.append(", ");                       
            }
        }
        return sb.toString();
    }
}
