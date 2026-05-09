package com.warung.haryati.model;

import java.util.List;

public class AnalisisResult {
    private List<FrequentItemset> frequent_itemsets;
    private List<AssociationRule> association_rules;
    private String error;

    public List<FrequentItemset> getFrequent_itemsets() { return frequent_itemsets; }
    public void setFrequent_itemsets(List<FrequentItemset> frequent_itemsets) { this.frequent_itemsets = frequent_itemsets; }

    public List<AssociationRule> getAssociation_rules() { return association_rules; }
    public void setAssociation_rules(List<AssociationRule> association_rules) { this.association_rules = association_rules; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public static class FrequentItemset {
        private double support;
        private List<String> itemsets;

        public double getSupport() { return support; }
        public void setSupport(double support) { this.support = support; }

        public List<String> getItemsets() { return itemsets; }
        public void setItemsets(List<String> itemsets) { this.itemsets = itemsets; }
        
        public String getItemsetsString() {
            return String.join(", ", itemsets);
        }
    }

    public static class AssociationRule {
        private List<String> antecedents;
        private List<String> consequents;
        private double support;
        private double confidence;
        private double lift;

        public List<String> getAntecedents() { return antecedents; }
        public void setAntecedents(List<String> antecedents) { this.antecedents = antecedents; }

        public List<String> getConsequents() { return consequents; }
        public void setConsequents(List<String> consequents) { this.consequents = consequents; }

        public double getSupport() { return support; }
        public void setSupport(double support) { this.support = support; }

        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }

        public double getLift() { return lift; }
        public void setLift(double lift) { this.lift = lift; }
        
        public String getAntecedentsString() { return String.join(", ", antecedents); }
        public String getConsequentsString() { return String.join(", ", consequents); }
    }
}
