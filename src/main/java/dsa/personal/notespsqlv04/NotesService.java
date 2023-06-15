package dsa.personal.notespsqlv04;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    public List<Notes> getAllNotes() {
        List<Notes> allNotes = new ArrayList<Notes>();
        notesRepository.findAll().forEach(allNotes::add);
        return allNotes;
    }

    public void addNote(Notes note) {
        notesRepository.save(note);
    }

    public void deleteNote(Long id) {
        notesRepository.deleteById(id);
    }

    public Optional<Notes> getNotes(Long id) {
        return notesRepository.findById(id);
    }

}