package dsa.personal.notespsqlv04;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for Notes management with permission-based access control.
 * Uses dynamic RBAC permissions defined in permissions.yml
 */
@RestController
public class NotesController {

	private static final Logger logger = LoggerFactory.getLogger(NotesController.class);

    @Autowired
    private NotesService notesService;

    /**
     * Create a new note
     * Requires NOTES:CREATE permission
     */
    @PostMapping("/notes")
    @PreAuthorize("hasAuthority('NOTES:CREATE')")
    public void addNote(@RequestBody Notes note) {
        logger.info("Creating new note");
        notesService.addNote(note);
        return;
    }

    /**
     * Update an existing note
     * Requires NOTES:MODIFY permission
     */
    @PutMapping("/notes/{id}")
    @PreAuthorize("hasAuthority('NOTES:MODIFY')")
    public void updateNote(@RequestBody Notes note, @PathVariable Long id) {
        logger.info("Updating note with id: {}", id);
	note.setId(id);
        notesService.addNote(note);
        return;
    }

    /**
     * Get all notes
     * Requires NOTES:READ permission
     */
    @GetMapping("/notes")
    @PreAuthorize("hasAuthority('NOTES:READ')")
    public List<Notes> getNotes() {
        logger.debug("Fetching all notes");
        return notesService.getAllNotes();
    }

    /**
     * Get a specific note by ID
     * Requires NOTES:READ permission
     */
    @GetMapping("/notes/{id}")
    @PreAuthorize("hasAuthority('NOTES:READ')")
    public Optional<Notes> getNote(@PathVariable Long id) {
        logger.debug("Fetching note with id: {}", id);
        return notesService.getNotes(id);
    }

    /**
     * Delete a note
     * Requires NOTES:DELETE permission
     */
    @DeleteMapping("/notes/{id}")
    @PreAuthorize("hasAuthority('NOTES:DELETE')")
    public void deleteNote(@PathVariable Long id) {
        logger.info("Deleting note with id: {}", id);
        notesService.deleteNote(id);
        return;
    }

    /**
     * Health check endpoint - no authentication required
     */
    @GetMapping("/ping")
    public String greet() {
        logger.debug("Grüezi, freut mich");
        return "Ciao!";
    }
}
