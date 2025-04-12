package application;


import java.sql.SQLException;


import databasePart1.DatabaseHelper;


/**
 * The {@code reviews} class represents a review made by a user, either on a question or an answer.
 * A review is associated with:
 * <ul>
 *     <li>A question ID</li>
 *     <li>Optionally an answer (if the review is on a specific answer)</li>
 *     <li>The author of the review</li>
 *     <li>The review text</li>
 * </ul>
 */
public class reviews {
	/**This id is for the reviews*/
	private int reviewId;
	/** The ID of the question this review is associated with. */
	private int id;
    /** The answer this review is about (may be {@code null} if it's a question-level review). */
	private String answer;
	/** The username of the author who wrote the review. */
	private String author;
	/** The content of the review. */
	private String review;
	/**The rate is the number of rating*/
	private int thumbsUp=0;
	
	
	/**
     * Constructs a new {@code reviews} object.
     *
     * @param id     the ID of the question associated with the review
     * @param answer the answer being reviewed (nullable if review is for the question)
     * @param author the author of the review
     * @param review the review content
     */
	public reviews(int reviewId, int id, String answer, String author, String review, int thumbsUp) {
		this.reviewId = reviewId;
		this.id=id;
		this.answer = answer;
		this.author = author;
		this.review = review;
		this.thumbsUp = thumbsUp;
		
	}
	
	/**
     * Returns the ID of the associated question.
     *
     * @return the question ID
     */
	public int getId() {
		return id;
	}
	
	
	 /**
     * Returns the answer this review is associated with.
     * This may be {@code null} if the review is for a question.
     *
     * @return the answer text or {@code null}
     */
	public String getAnswer() {
		return answer;
	}
	
	
	 /**
     * Returns the username of the author who wrote the review.
     *
     * @return the author's username
     */
	public String getAuthor() {
		return author;
	}
	
	
	 /**
     * Returns the content of the review.
     *
     * @return the review text
     */
	public String getReview() {
		return review;
	}
	
	public int getThumbsUp() {
		return thumbsUp;
	}
	
	public void setThumbsUp(int thumbsUp) {
		this.thumbsUp = thumbsUp;
	}
	
	public int getReviewId() {
		return reviewId;
	}
}