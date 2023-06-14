package dsa.personal.notespsqlv04;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.sql.Timestamp;

@Entity
public class Notes {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	private String title;
	private String note;
	private Timestamp timestamp;
	@Version
	private int version;

	public Notes() {
	}
	
	public Notes(long id, String title, String note, Timestamp timestamp) {
		super();
		this.id = id;
		this.title = title;
		this.note = note;
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Notes [id=" + id + ", title=" + title + ", note=" + note + ", timestamp=" + timestamp + "]";
	}

}
