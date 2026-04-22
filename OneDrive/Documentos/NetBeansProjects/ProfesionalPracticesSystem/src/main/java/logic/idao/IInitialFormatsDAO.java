/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package logic.idao;


import java.util.List;
import logic.businessObject.InitialFormats;
import logic.exceptions.DAOException;


/**
 *
 * @author Jhonatan Yeray Hernadez Rivera
 * @version1.0
 */
public interface IInitialFormatsDAO {
    boolean registerInitialFormats(InitialFormats initialFormats) throws DAOException;
     
    List<InitialFormats> getByStudentMatricula(String matricula)throws DAOException;
      
    List<InitialFormats> getByTypeOfDocument(String typeOfDocument)throws DAOException;
}
