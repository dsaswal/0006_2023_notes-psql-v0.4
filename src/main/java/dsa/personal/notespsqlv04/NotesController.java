package dsa.personal.notespsqlv04;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public void updateNote(@RequestBody Notes note, @PathVariable Long id) {
	note.setId(id);
        notesService.addNote(note);
        return;
    }

    @GetMapping("/notes")
    public List<Notes> getNotes() {
        return notesService.getAllNotes();
    }

    @GetMapping("/notes/{id}")
    public Optional<Notes> getNote(@PathVariable Long id) {
        return notesService.getNotes(id);
    }

    @DeleteMapping("/notes/{id}")
    public void deleteNote(@PathVariable Long id) {
        notesService.deleteNote(id);
        return;
    }

    @GetMapping("/ping")
    public String greet() {
        logger.debug("Grüezi, freut mich");
        return "Ciao!";
    }

    @GetMapping("/error")
    public void errorMsg() {
        logger.debug("Manchmal schlecht");
        new Exception("BAD_REQUEST");
    }
}
