package fr.gamaticow.hashcode;

/**
 * Created by Corentin on 24/02/2022 at 19:21
 */

public class Project implements Comparable<Project> {

    public String name;
    public int nbDays;
    public int score;
    public int bestBefore;
    public Role[] roles;

    public float priority;

    @Override
    public int compareTo(Project o) {
        return Float.compare(priority, o.priority);
    }
}
