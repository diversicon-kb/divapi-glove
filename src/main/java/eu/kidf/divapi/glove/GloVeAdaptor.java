package eu.kidf.divapi.glove;

import de.jungblut.distance.CosineDistance;
import de.jungblut.glove.GloveRandomAccessReader;
import de.jungblut.glove.impl.GloveBinaryRandomAccessReader;
import de.jungblut.math.DoubleVector;
import eu.kidf.divapi.Concept;
import eu.kidf.divapi.Domain;
import eu.kidf.divapi.IDivAPI;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Adaptor implementing the DivAPI interface for accessing
 * GloVe-based word embeddings. Uses the Thomas Jungblut's Java implementation
 * of GloVe, https://github.com/thomasjungblut/glove
 * 
 * @author Gábor BELLA
 */
public class GloVeAdaptor implements IDivAPI {
    
    private final Double DEFAULT_RELATEDNESS_THRESHOLD = 0.70;
    
    private final GloveRandomAccessReader dic;
    private Double threshold;
    
    public GloVeAdaptor(String pathToResource, Double threshold) throws IOException {
        this(pathToResource);
        this.threshold = threshold; 
    }

    public GloVeAdaptor(String pathToResource) throws IOException {
        try {
            dic = new GloveBinaryRandomAccessReader(Paths.get(pathToResource));
            threshold = DEFAULT_RELATEDNESS_THRESHOLD;
        } catch (Exception e) {
            throw e;
        }        
    }

    @Override
    public Set<String> getRelatedWords(String language, Domain domain, String word, WordRelation rel) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<String, Double> getRelatedWordsWeighted(String language, Domain domain, String word, WordRelation rel) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<WordRelation> getRelations(String language, Domain domain, String word1, String word2) {
        Map<WordRelation, Double> relMap = getRelationsWeighted(language, domain, word1, word2);
        Set<WordRelation> relSet = new HashSet<>();
        for(WordRelation rel : relMap.keySet()) {
            if (relMap.get(rel) < threshold) {
                relSet.add(rel);
            }
        }
        return relSet;
    }

    @Override
    public Map<WordRelation, Double> getRelationsWeighted(String language, Domain domain, String word1, String word2) {
        Map<WordRelation, Double> relMap = new HashMap<>();
        Double sim;
        try {
            sim = getSimilarity(dic.get(word1), dic.get(word2));
        } catch (IOException e) {
            sim = 0.0;
        }
        relMap.put(IDivAPI.WORD_RELATEDNESS, sim);
        relMap.put(IDivAPI.WORD_SIMILARITY, sim);
        return relMap;
    }

    @Override
    public Set<String> getLanguages(Domain domain, String word) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Domain> getDomains(String language, String word) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Domain, Double> getDomainsWeighted(String language, String word, Set<Domain> domains) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Concept> getConcepts(String language, Domain domain, String word) {
        Set<Concept> result = new HashSet<>();
        WordVector vector = getWordVector(word);
        if (vector != null) {
            result.add(new Concept(vector, word));
        }
        return result;
    }

    @Override
    public Map<Concept, Double> getConceptsWeighted(String language, Domain domain, String word) {
        Map<Concept, Double> conceptMap = new HashMap<>();
        Set<Concept> conceptSet = getConcepts(language, domain, word);
        if (conceptSet.isEmpty()) {
            return conceptMap;
        }
        Concept concept = conceptSet.iterator().next();
        conceptMap.put(concept, 1.0);
        return conceptMap;
    }

    @Override
    public Set<Concept> getConstrainedConcepts(String language, Domain domain, String word, Concept hypernymConcept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Concept, Double> getConstrainedConceptsWeighted(String language, Domain domain, String word, Concept hypernymConcept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<String> getWords(String language, Concept concept) {
        Set<String> words = new HashSet<>();
        if (concept == null) {
            return null;
        }
        words.add(concept.getID());
        return words;
    }

    @Override
    public Map<String, Double> getWordsWeighted(String language, Concept concept) {
        Map<String, Double> words = new HashMap<>();
        if (concept == null) {
            return null;
        }
        words.put(concept.getID(), 1.0);
        return words;
    }

    @Override
    public String getGloss(String language, Concept concept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Concept> getRelatedConcepts(Concept concept, Set<ConceptRelation> relations) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Concept, Double> getRelatedConceptsWeighted(Concept concept, Set<ConceptRelation> relations) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<ConceptRelation> getRelations(Concept c1, Concept c2) {
        Map<ConceptRelation, Double> relMap = getRelationsWeighted(c1, c2);
        Set<ConceptRelation> relSet = new HashSet<>();
        for(ConceptRelation rel : relMap.keySet()) {
            if (relMap.get(rel) < threshold) {
                relSet.add(rel);
            }
        }
        return relSet;
    }

    @Override
    public Map<ConceptRelation, Double> getRelationsWeighted(Concept c1, Concept c2) {
        Map<ConceptRelation, Double> relMap = new HashMap<>();
        if (c1 == null || c2 == null) {
            return relMap;
        }
        DoubleVector v1 = (DoubleVector) c1.getContainer();
        DoubleVector v2 = (DoubleVector) c2.getContainer();
        Double sim = getSimilarity(v1, v2);
        relMap.put(IDivAPI.CONCEPT_RELATEDNESS, sim);
        relMap.put(IDivAPI.CONCEPT_SIMILARITY, sim);
        return relMap;
    }

    @Override
    public Set<String> getLanguages(Domain domain, Concept concept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Domain> getDomains(Concept concept) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<Domain, Double> getDomainsWeighted(Concept concept) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getLanguages() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<Domain> getDomains() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private WordVector getWordVector(String word) {
        DoubleVector vector;
        try {
            vector = dic.get(word);
        } catch (IOException e) {
            return null;
        }
        if (vector == null) {
            return null;
        }
        WordVector wordVector = new WordVector(word, vector); 
        return wordVector;
    }
    
    private Double getSimilarity(DoubleVector v1, DoubleVector v2) {
        CosineDistance cos = new CosineDistance();
        Double dist = cos.measureDistance(v1, v2);
        return normalizeSimilarity(dist);
    }
    
    private Double normalizeSimilarity(Double similarity) {
        similarity = 1.0 - similarity;
        if (similarity < 0.0) {
            similarity = 0.0;
        }
        return similarity;
    }

}
