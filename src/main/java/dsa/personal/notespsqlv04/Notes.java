package dsa.personal.notespsqlv04;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Notes {
	
	@Id
    private long id;
	private String title;
	private String note;
	private long timestamp;
	
	public Notes(long id, String title, String note, long timestamp) {
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Notes [id=" + id + ", title=" + title + ", note=" + note + ", timestamp=" + timestamp + "]";
	}

}
