package dsa.personal.notespsqlv04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotesController {

    @Autowired
    private NotesService notesService;

    @PostMapping
    public void addNote(Notes note) {
        notesService.addTopic(note);
        return;
    }
    
}
