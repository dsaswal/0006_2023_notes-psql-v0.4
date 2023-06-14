package dsa.personal.notespsqlv04;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.PrePersist;
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
	
	public Notes(String title, String note) {
		super();
		this.title = title;
		this.note = note;
	}

    	@PrePersist
    	protected void onCreate() {
        	timestamp = new Timestamp(System.currentTimeMillis());
    	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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
