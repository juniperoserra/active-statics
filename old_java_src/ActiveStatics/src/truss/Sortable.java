/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: Sortable.java,v 1.1 2002/09/12 21:36:42 simong Exp $
 */
package truss;


/**
 * This interface is only to sort an array with an abstract algorithm.
 *
 * @version $Revision: 1.1 $
 * @author  Philippe Le Hégaret
 */
public interface Sortable {

    /**
     * The sort function.
     *
     * @param objs the array with all objects
     * @param start the start offset in the array
     * @param end the end offset in the array
     * @param comp The comparaison function between objects
     */
    public void sort(Object[] objs, int start, int end, Comparable comp);
}
