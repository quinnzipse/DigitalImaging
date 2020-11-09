package hw3;

public class ImageSimilarity implements Comparable<ImageSimilarity> {
    public double distance;
    public final String author;
    public final String thumbnail;
    public final String image;

    public ImageSimilarity(String author, String thumbnail, String image) {
        this(0, author, thumbnail, image);
    }

    public ImageSimilarity(double distance, String author, String thumbnail, String image) {
        this.author = author;
        this.distance = distance;
        this.thumbnail = thumbnail;
        this.image = image;
    }

    @Override
    public int compareTo(ImageSimilarity o) {
        if (distance - o.distance < 0) return -1;
        else if (distance - o.distance == 0) return 0;
        return 1;
    }
}
