package dsa.personal.notespsqlv04;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public void addNote(@RequestBody Notes note) {
        notesService.addNote(note);
        return;
    }

    @PutMapping("/notes/{id}")
    public void updateNote(@RequestBody Notes note, @PathVariable long id) {
	note.setId(id);
        notesService.addNote(note);
        return;
    }

    @GetMapping("/notes")
    public List<Notes> getNotes() {
        return notesService.getAllNotes();
    }

    @GetMapping("/notes/{id}")
    public Notes getNote(@PathVariable long id) {
        return notesService.getNotes(id);
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
