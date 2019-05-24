package com.michalraq.proximitylightapp.data.estimote;

/**
 * Klasa przechowująca dane nadajnika. Używana w momencie wyświetlania listy z nadajnikami znajdującymi się w pobliżu.
 * Nieużywana w przypadku pracy demonstratora.
 */
public class ProximityContent {

    private String title;
    private String attachment;

   public ProximityContent(String title, String attachment) {
        this.title = title;
        this.attachment = attachment;
    }

   public String getTitle() {
        return title;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
