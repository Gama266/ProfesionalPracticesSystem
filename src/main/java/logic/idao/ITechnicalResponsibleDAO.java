
package logic.idao;

import java.util.List;
import logic.businessObject.TechnicalResponsible;
import logic.exceptions.DAOException;

/**
 *
 * @author gamal
 */
public interface ITechnicalResponsibleDAO {
    boolean registerTechnicalResponsible(TechnicalResponsible newResponsible) throws DAOException;
    boolean updateTechnicalResponsible(TechnicalResponsible responsible) throws DAOException;
    boolean isEmailAlreadyRegistered(String email) throws DAOException;
    List<TechnicalResponsible> getAllTechnicalResponsibles() throws DAOException;
    List<TechnicalResponsible> getByOrganization(int idOrganization) throws DAOException;
}
