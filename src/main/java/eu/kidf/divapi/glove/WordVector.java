package eu.kidf.divapi.glove;

import de.jungblut.math.DoubleVector;

/**
 * GloVe word vectors labelled with their words.
 * 
 * @author Gábor BELLA
 */
class WordVector {
    private String word;
    private DoubleVector vector;
    
    public WordVector(String w, DoubleVector v) {
        word = w;
        vector = v;
    }
    
    public String getWord() {
        return word;
    }
    
    public DoubleVector getVector() {
        return vector;
    }
}
