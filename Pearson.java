package ucd.ai.cf;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to compute the pearson profile similarity metric and also
 * to predict ratings 3 methods in this class need to be implemented by the
 * student.
 */
public class Pearson implements SimilarityMetric {

	/**
	 * Constructor for Pearson
	 * 
	 * @param profileSet
	 *            the set of profiles on which Pearson will operate Examples of
	 *            use: Set profiles = .... load from files Pearson pearson = new
	 *            Pearson(profiles);
	 */
	public Pearson(final Set<Profile> profileSet) {
		setup(profileSet);
	}

	/**
	 * YOU SHOULD IMPLEMENT THIS METHOD Computes the pearson correlation
	 * coefficient (similarity) between 2 profiles. This has to be implemented
	 * by the student.
	 * 
	 * @param a
	 *            The first profile to compare
	 * @param b
	 *            The second profile
	 * @return the pearson profile similarity between the 2 profiles
	 */
	public double computeSimilarity(final Profile a, final Profile b) {
		Set<Movie> commonMovies = a.getCommonMovies(b);
		double aAverageRating = a.getMeanRating();
		double bAverageRating = b.getMeanRating();
		double top = 0;
		double bottomA = 0;
		double bottomB = 0;
		for (Movie movie : commonMovies) {
			double ad = a.getRatingFor(movie) - aAverageRating;
			double bd = b.getRatingFor(movie) - bAverageRating;
			top += (ad * bd);
			bottomA += (ad * ad);
			bottomB += (bd * bd);
		}
		double bottom = Math.sqrt(bottomA * bottomB);
		if (bottom > 0) {
			if (commonMovies.size() < 50) {
				return (commonMovies.size() * 1.0 / 50) * (top / bottom);
			} else {
				return top / bottom;
			}
		} else {
			return 0;
		}
	}

	/**
	 * Predicts the rating for a movie for the given profile using the Pearson
	 * similarity metric
	 * 
	 * @param profile
	 *            the profile for which the rating will be prdicted
	 * @param movie
	 *            the movie for which the rating will be made
	 * @param simThreshold
	 *            the maximum dissimilarity threshold (<i>L</i> as described in
	 *            Social Information Filtering)
	 * @return the predicted rating that the owner of that profile would have
	 *         made for that movie
	 */
	public double predictRating(final Profile profile, final Movie m, final double minThreshold) {
		double first = 0;
		double second = 0;
		// for each neighbours of target profile
		for(Profile p:this.computeNeighbours(profile, minThreshold)){
			if(p.hasRated(m)){
			first=first+(this.computeSimilarity(profile, p) *(p.getRatingFor(m)-p.getMeanRating()));
			second=second+this.computeSimilarity(profile, p);
			}
		}
		if(second>0){
			return (profile.getMeanRating()+(first/second));
		}else
			return 0;
	}

	/**
	 * Computes the set of neighbours that will be used in the prediction of a
	 * movie rating for a given user. Note that its suggested that you implement
	 * this method but not necessary
	 * 
	 * @param profile
	 *            The profile for which the neighbourhood will be found
	 * @param simThreshold
	 *            the maximum dissimilarity threshold for the neigbours
	 * @return the set of neighbours that are most similar to the given profile
	 */
	protected Set<Profile> computeNeighbours(final Profile profile, final double simThreshold) {
		//For each profile I will execute compute similarity 
		Set<Profile> result = new HashSet<Profile>();
		for(Profile p:this.getProfileSet()){
			//I keep the target profile and for each other profile I check the similarity I get the MSD
			//I keep all profiles that the MSD compute similarity is greater than simThreshold
			//I jump if the profile is the same 
			if(this.computeSimilarity(profile, p)>simThreshold && ! profile.equals(p)){
				result.add(p);
			}
		}
		return result;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// DO NOT EDIT BELOW THIS LINE UNLESS YOU KNOW WHAT YOU ARE DOING!
	/////////////////////////////////////////////////////////////////////////////////////////

	private static double MIN_RATING = 1;
	private static double MAX_RATING = 5;
	private double[][] simMatrix = null;// hold all the computed Pearson values
	private Set<Profile> profileSet;

	private void setup(final Set<Profile> profiles) {
		simMatrix = new double[profiles.size()][profiles.size()];
		this.profileSet = profiles;
		computeAllSimilarity(profiles);
	}

	private void computeAllSimilarity(final Set<Profile> set) {
		for (Profile a : set) {
			for (Profile b : set) {
				setSim(a, b, computeSimilarity(a, b));
			}
		}
	}

	/**
	 * Stores the Pearson correlation coefficent (similary value) in memory
	 * between 2 profiles
	 * 
	 * @param a
	 *            First profile
	 * @param b
	 *            Second profile
	 * @param the
	 *            similarity between to 2.
	 * 
	 *            Note - do not edit this method or your code WILL fail.
	 */
	private void setSim(final Profile a, final Profile b, final double value) {
		simMatrix[a.internalID()][b.internalID()] = value;
		simMatrix[b.internalID()][a.internalID()] = value;
	}

	/**
	 * Retrieves the previously computed Pearson value between 2 profiles from
	 * memory
	 * 
	 * @param First
	 *            profile
	 * @param Second
	 *            profile
	 * @return the MSD value for the 2 profiles
	 * 
	 *         Note - do not edit this method or your code WILL fail.
	 */
	private double getPearson(final Profile profile, final Profile candidate) {
		return simMatrix[profile.internalID()][candidate.internalID()];
	}

	/**
	 * @return Returns the set of profiles that the similarity metric is working
	 *         on.
	 */
	public Set<Profile> getProfileSet() {
		return profileSet;
	}

	/**
	 * Computes the average rating given by a user for a set of given movies
	 * Note that its suggested that you implement this method but not necessary
	 * 
	 * @param profile
	 *            The profile in question
	 * @param commonMovies
	 *            The set of movies for which ratings were given
	 * @return the average rating given by user p for the set of movies
	 */
	protected double calcAverageRating(final Profile profile, final Set<Movie> commonMovies) {
		double total = 0;
		for (Movie movie : commonMovies) {
			total = total + profile.getRatingFor(movie);
		}
		return total / commonMovies.size();
	}
}
