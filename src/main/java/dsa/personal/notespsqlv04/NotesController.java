package dsa.personal.notespsqlv04;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotesController {

    @Autowired
    private NotesService notesService;

    @PostMapping
    public void addNote(Notes note) {
        notesService.addNote(note);
        return;
    }

    @GetMapping("/notes")
    public List<Notes> getNotes() {
        return notesService.getAllNotes();
    }

}
