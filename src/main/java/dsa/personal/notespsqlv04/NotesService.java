@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    public List<> getAllTopics() {
        List<Notes> allNotes = new ArrayList();
        notesRepository.findAll().forEach(topics::add);
        return allNotes;
    }

    public void addTopic(Notes note) {
        notesRepository.save(note);
    }

}