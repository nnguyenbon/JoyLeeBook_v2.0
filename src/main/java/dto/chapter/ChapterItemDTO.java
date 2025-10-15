package dto.chapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * ChapterListItem class represents a chapter item in a list view.
 * It includes details about the chapter and its associated series.
 *
 * @author HaiDD-dev
 */
public class ChapterItemDTO extends BaseChapterDTO{
    private int seriesId;
    private String seriesTitle;
    private String coverImgUrl;
    private LocalDateTime lastReadAt;

    public Date getLastReadAtAsDate() {
        return lastReadAt != null ? Date.from(lastReadAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }


    public String getLastReadAtFormatted() {
        return lastReadAt != null
                ? lastReadAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
    }

}
