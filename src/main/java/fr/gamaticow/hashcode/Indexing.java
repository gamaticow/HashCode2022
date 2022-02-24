package fr.gamaticow.hashcode;

import java.util.*;

/**
 * Created by Corentin on 24/02/2022 at 20:16
 */

public class Indexing {

    private Map<String, List<Contributor>> iContributorSkill = new HashMap<>();

    public Indexing(DataSet dataSet) {
        for(Contributor contributor : dataSet.contributors) {
            for(Skill skill : contributor.skills) {
                if(!iContributorSkill.containsKey(skill.name)) {
                    iContributorSkill.put(skill.name, new ArrayList<>());
                }
                iContributorSkill.get(skill.name).add(contributor);
            }
        }
    }

    public List<Contributor> getContributorsBySkill(String skill) {
        if(!iContributorSkill.containsKey(skill))
            return new ArrayList<>();

        return new ArrayList<>(iContributorSkill.get(skill));
    }

}
