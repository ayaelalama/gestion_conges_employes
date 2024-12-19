package DAO;

import java.util.List;
public interface GenericDAO<T> {
    void add(T entity); // Ajouter un objet
    void delete(int id); // Supprimer un objet par ID
    List<T> listAll(); // Lister tous les objets
    T findById(int id); // Trouver un objet par ID
    void update(T entity, int id); // Mettre à jour un objet
}
