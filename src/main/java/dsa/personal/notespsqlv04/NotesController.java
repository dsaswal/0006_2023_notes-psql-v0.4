package dsa.personal.notespsqlv04;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class NotesController {

	private static final Logger logger = LoggerFactory.getLogger(NotesController.class);

    @Autowired
    private NotesService notesService;
    
    @PostMapping("/notes")
    public void addNote(Notes note) {
        notesService.addNote(note);
        return;
    }

    @GetMapping("/notes")
    public List<Notes> getNotes() {
        return notesService.getAllNotes();
    }

    @GetMapping("/ping")
    public String greet() {
        logger.debug("This is a debug statement");
        return "Ciao!";
    }

    @GetMapping("/error")
    public String errorMsg() {
        logger.debug("This is a debug statement");
        return "Ciao!";
    }
}
