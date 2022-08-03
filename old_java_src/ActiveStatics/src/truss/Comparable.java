/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: Comparable.java,v 1.1 2002/09/12 21:36:42 simong Exp $
 */
package truss;

/**
 * The comparaison function for the Sortable class
 *
 * @version $Revision: 1.1 $
 * @author  Philippe Le Hégaret
 * @see Sortable
 */
public interface Comparable {
    public boolean compare(Object obj1, Object obj2);
}
