package gaddam1987.github.persistance;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.util.CollectionUtils;

/**
 * This class provides all the functionality that hibernate offers on persistent classes ex. save,update,read,delete
 * etc.
 *
 * @author Shrey Khandelwal
 */
public abstract class BaseDAOHibernateImpl<T, ID extends Serializable>
// implements BaseDAO<T, ID>
{


    @Autowired
    private SessionFactory factory;

    private String thisClass = "BaseDAOHibernateImpl";

    private HibernateTemplate template;

    /**
     * Return the session if not null else get the session from SessionFactory and return the same
     *
     * @return - session object
     * @throws DatabaseException
     */
    public Session getSession()
            throws DatabaseException
    {
        String thisMethod = "getSession";
        try
        {
            if ( factory.getCurrentSession() != null )
            {
                return factory.getCurrentSession();
            }
            else
            {
                return factory.openSession();
            }

        }
        catch ( NullPointerException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9004" );
        }
        catch ( SessionException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9005" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

    }

    /**
     * Used to save the entity object in to database
     *
     * @param objEntity - entity object to save in database
     * @return entity object which is saved in database
     * @throws DatabaseException
     */
    public T save( T objEntity )
            throws DatabaseException
    {
        String thisMethod = "save";

        try
        {
            getSession().save( objEntity );
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }
        return objEntity;
    }

    public List<T> save( List<T> objEntity, int batchSize )
            throws DatabaseException
    {
        String thisMethod = "save";

        try
        {
            int count = 0;

            for ( T objEnttyIdntfr : objEntity )
            {
                getSession().save( objEnttyIdntfr );
                count++;
                if ( count % batchSize == 0 )
                {

                    getSession().flush();
                    getSession().clear();
                }
            }

        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9003" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objEntity;
    }

    /**
     * Used to save the entity object in to database It commits the transaction
     *
     * @param objEntity - entity object to save in database
     * @return entity object saved in database
     * @throws DatabaseException
     */
    public T saveAndCommit( T objEntity )
            throws DatabaseException
    {
        String thisMethod = "saveAndCommit";

        try
        {
            Session objSession = getSession();
            Transaction objTransaction = objSession.beginTransaction();
            objSession.save( objEntity );
            objTransaction.commit();

        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objEntity;
    }

    /**
     * Used to saveOrUpdate the entity object in to database
     *
     * @param objEntity - entity object to save or update in database
     * @return entity object saved or updated in database
     * @throws DatabaseException
     */
    public T saveOrUpdate( T objEntity )
            throws DatabaseException
    {
        String thisMethod = "saveOrUpdate";

        try
        {
            template = new HibernateTemplate( factory );
            template.saveOrUpdate( objEntity );
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objEntity;
    }

    public T saveOrUpdateAndFlush( T objEntity )
            throws DatabaseException
    {
        String thisMethod = "saveOrUpdateAndFlush";

        try
        {
            template = new HibernateTemplate( factory );
            template.saveOrUpdate( objEntity );
            template.flush();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objEntity;
    }

    /**
     * Used to remove the entity object in to database
     *
     * @param objEntity - entity object to remove from database
     * @return status message of remove operation.
     * @throws DatabaseException
     */
    public boolean remove( T objEntity )
            throws DatabaseException
    {
        String thisMethod = "remove";
        boolean status = false;

        try
        {
            getSession().delete( objEntity );
            status = true;
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return status;
    }

    /**
     * Used to find the record by its ID
     *
     * @param objId - id of record to be fetched
     * @return Object fetched from database if present in database else return null
     * @throws DatabaseException
     */
    public T findById( ID objId, Class<T> objPersistentClass )
            throws DatabaseException
    {

        String thisMethod = "findById";

        T objEntity = null;
        try
        {
            objEntity = findById( objId, false, objPersistentClass );

        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objEntity;
    }

    /**
     * Used to find the record by its ID and apply lock on it
     *
     * @param objId - id of record to be fetched
     * @return Object fetched from database if present in database else return null
     * @throws DatabaseException
     */
    @SuppressWarnings( { "unchecked", "deprecation" } )
    public T findById( ID objId, boolean boolLock, Class<T> objPersistentClass )
            throws DatabaseException
    {

        String thisMethod = "findById";

        T objEntity = null;
        try
        {

            Session objSession = getSession();
            if ( boolLock )
            {
                objEntity = (T) objSession.get( objPersistentClass, objId, LockMode.UPGRADE );
            }
            else
            {
                objEntity = (T) objSession.get( objPersistentClass, objId );
            }
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objEntity;
    }

    /**
     * Used to fetch all the records from the table
     *
     * @return List of all records present in database for class specified by objPersistentClass
     * @throws DatabaseException
     */
    public List<T> findAll( Class<T> objPersistentClass )
            throws DatabaseException
    {
        String thisMethod = "findAll";

        List<T> listOfData = null;
        try
        {
            listOfData = findByCriteria( objPersistentClass );
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    public long fetchRowCount( Class<T> objPersistentClass, Criterion... criterions )
            throws DatabaseException
    {
        String thisMethod = "fetchRowCount";

        Criteria criteria = null;

        try
        {
            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            criteria.setProjection( Projections.rowCount() );

            return (Long) criteria.uniqueResult();

        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }
    }

    protected long fetchRowCount( List<Criterion> criterions, Class<T> objPersistentClass )
            throws DatabaseException
    {
        String thisMethod = "fetchRowCount";

        Criteria criteria = null;

        try
        {
            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            criteria.setProjection( Projections.rowCount() );

            return (Long) criteria.uniqueResult();

        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

    }

    /**
     * Used to fetch the data using Criteria query
     *
     * @param criterions - array of Criterion
     * @return list of records fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( Class<T> objPersistentClass, Criterion... criterions )
            throws DatabaseException
    {
        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {

            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;
    }

    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( Order orderBy, Integer firstResult, Integer numberOfRecords,
                                      Class<T> objPersistentClass, Criterion... criterions )
            throws DatabaseException
    {
        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {
            criteria = getSession().createCriteria( objPersistentClass );

            if(null!=criterions){
                for ( Criterion criterion : criterions )
                {
                    if ( criterion != null )
                    {
                        criteria.add( criterion );
                    }
                }
            }
            if ( null != orderBy )
                criteria.addOrder( orderBy );
            if ( firstResult != null )
                criteria.setFirstResult( firstResult );
            if ( numberOfRecords != null )
                criteria.setMaxResults( numberOfRecords );

            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;
    }

    /**
     * @param criterions
     * @return
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( List<Criterion> criterions, Class<T> objPersistentClass )
            throws DatabaseException
    {
        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {

            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;
    }

    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteriaWithAlias( List<Criterion> criterions, String alias, Class<T> objPersistentClass )
            throws DatabaseException
    {
        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {

            criteria = getSession().createCriteria( objPersistentClass );
            criteria.createAlias( alias, alias );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;
    }

    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( List<String> aliasList, Order orderBy, Integer firstResult,
                                      Integer numberOfRecords, Class<T> objPersistentClass, Criterion... criterions )
            throws DatabaseException
    {
        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {
            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            if ( aliasList != null )
            {
                for ( String alias : aliasList )
                {
                    criteria.createAlias( alias, alias );
                }
            }

            criteria.addOrder( orderBy );
            if ( firstResult != null )
                criteria.setFirstResult( firstResult );
            if ( numberOfRecords != null )
                criteria.setMaxResults( numberOfRecords );

            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;
    }

    /**
     * @param criterions
     * @return
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( Order order1, Order order2, int offset, int noOfRecords,
                                      Class<T> objPersistentClass, Criterion... criterions )
            throws DatabaseException
    {

        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {
            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                criteria.add( criterion );
            }
            if ( offset >= 0 )
            {
                criteria.setFirstResult( offset );
                criteria.setMaxResults( noOfRecords );
            }
            if ( order1 != null && order2 != null )
            {
                criteria.addOrder( order1 ).addOrder( order2 );
            }
            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {

            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );

        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;

    }

    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( Order order1, Integer offset, Integer noOfRecords, Class<T> objPersistentClass,
                                      List<Criterion> criterions )
            throws DatabaseException
    {

        String thisMethod = "findByCriteria";

        Criteria criteria = null;
        List<T> listOfObjects = null;
        try
        {
            criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                criteria.add( criterion );
            }
            if ( offset != null )
            {
                criteria.setFirstResult( offset );
            }

            if ( noOfRecords != null )
            {
                criteria.setMaxResults( noOfRecords );
            }
            if ( order1 != null )
            {
                criteria.addOrder( order1 );
            }
            listOfObjects = criteria.list();
        }
        catch ( HibernateException e )
        {

            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );

        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());

            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfObjects;

    }

    /**
     * returns list of entity instance using named query
     *
     * @return list of records fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    public List<T> findWithNamedQuery( String strNamedQueryName )
            throws DatabaseException
    {
        String thisMethod = "findWithNamedQuery";

        List<T> listOfData = null;
        try
        {
            listOfData = factory.getCurrentSession().getNamedQuery( strNamedQueryName ).list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /**
     * returns list of entity instance using named query with parameters
     *
     * @throws DatabaseException
     */
    public List<T> findWithNamedQuery( String strNamedQueryName, Map<String, Object> mapParameters )
            throws DatabaseException
    {
        String thisMethod = "findWithNamedQuery";

        List<T> listOfData = null;
        try
        {
            listOfData = findWithNamedQuery( strNamedQueryName, mapParameters, 0 );
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }
        return listOfData;
    }

    /**
     * returns list of entity instance using named query with result limit
     *
     * @param resultLimit : int value specifies Maximum records to be fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    public List<T> findWithNamedQuery( String strNamedQueryName, int resultLimit )
            throws DatabaseException
    {

        String thisMethod = "findWithNamedQuery";

        List<T> listOfData = null;
        try
        {
            Query objSQLQuery = factory.getCurrentSession().getNamedQuery( strNamedQueryName );
            if ( resultLimit > 0 )
            {
                objSQLQuery.setMaxResults( resultLimit );
            }

            listOfData = objSQLQuery.list();

        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /**
     * returns list of entity instance using named query with parameters and result limit
     *
     * @param resultLimit : int value specifies Maximum records to be fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    public List<T> findWithNamedQuery( String strNamedQueryName, Map<String, Object> mapParameters, int resultLimit )
            throws DatabaseException
    {

        String thisMethod = "findWithNamedQuery";

        List<T> listOfData = null;
        try
        {

            Set<Entry<String, Object>> setRawParameters = mapParameters.entrySet();
            Query objSQLQuery = factory.getCurrentSession().getNamedQuery( strNamedQueryName );
            if ( resultLimit > 0 )
            {
                objSQLQuery.setMaxResults( resultLimit );
            }
            for ( Entry<String, Object> objEntry : setRawParameters )
            {
                objSQLQuery.setParameter( objEntry.getKey(), objEntry.getValue() );
            }
            listOfData = objSQLQuery.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /**
     * Used to fetch the records using HQL
     *
     * @param strHqlQuery - HQL Query to execute
     * @return list of records fetched from database as output of query strHqlQuery
     * @throws DatabaseException
     */
    @SuppressWarnings( "unchecked" )
    public List<T> findByHQLQuery( String strHqlQuery )
            throws DatabaseException
    {

        String thisMethod = "findByHQLQuery";

        List<T> listOfData = null;
        try
        {

            listOfData = getSession().createQuery( strHqlQuery ).list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    @SuppressWarnings( "unchecked" )
    public List<T> findByHQLQueryWithParams( String strHqlQuery, Map<String, String> mapParameters )
            throws DatabaseException
    {

        String thisMethod = "findByHQLQuery";

        List<T> listOfData = null;

        try
        {

            Set<Entry<String, String>> setRawParameters = mapParameters.entrySet();

            Query objQuery = getSession().createQuery( strHqlQuery );
            for ( Entry<String, String> objEntry : setRawParameters )
            {
                /*
                 * objQuery.setParameter(objEntry.getKey(), objEntry.getValue());
                 */
                if ( objEntry.getValue() != null && objEntry.getValue() instanceof String )
                {
                    objQuery.setParameter( objEntry.getKey(), objEntry.getValue() );
                }
            }

            listOfData = objQuery.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }



    public Object findUniqueByCriteria( Class<T> objPersistentClass, Criterion... criterions )
            throws DatabaseException
    {
        String thisMethod = "findUnique";

        Object result = null;
        try
        {
            Criteria criteria = getSession().createCriteria( objPersistentClass );
            for ( Criterion criterion : criterions )
            {
                if ( criterion != null )
                {
                    criteria.add( criterion );
                }
            }
            result = criteria.uniqueResult();
        }
        catch ( HibernateException he )
        {
            System.err.println(" Exception occured = " + he.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return result;
    }

    /**
     * Used to fetch unique records based on some criteria
     *
     * @param clazz
     * @param conditionsMap
     * @return
     */
    public Object findUnique( Class<T> clazz, Map<String, Object> conditionsMap )
            throws DatabaseException
    {
        String thisMethod = "findUnique";

        Object result = null;
        try
        {
            Criteria criteria = getSession().createCriteria( clazz );
            for ( String conditionKey : conditionsMap.keySet() )
            {
                criteria.add( Restrictions.eq( conditionKey, conditionsMap.get( conditionKey ) ) );
            }
            result = criteria.uniqueResult();
        }
        catch ( HibernateException he )
        {
            System.err.println(  " Exception occured = " + he.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return result;
    }

    /**
     * Used to update the records using HQL query
     *
     * @param strHqlQuery - HQL Query to execute
     * @return number of rows affected
     * @throws DatabaseException
     */
    public int executeUpdateByHqlQuery( String strHqlQuery )
            throws DatabaseException
    {

        String thisMethod = "executeUpdateByHqlQuery";

        int recordsUpdated = 0;
        try
        {
            recordsUpdated = getSession().createQuery( strHqlQuery ).executeUpdate();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return recordsUpdated;
    }

    /**
     * Used to execute Native Query
     *
     * @param strSqlQuery - native query to execute
     * @return list of records fetched from database
     * @throws DatabaseException
     */
    /**
     * Used to execute Native Query on given entity .
     *
     * @param strSqlQuery - native query to execute
     * @param objType - entity type
     * @return list of records fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public List<T> findByNativeQuery( String strSqlQuery, Class objType )
            throws DatabaseException
    {

        String thisMethod = "findByNativeQuery";

        List<T> listOfData = null;
        try
        {
            SQLQuery objSQLQuery = factory.getCurrentSession().createSQLQuery( strSqlQuery ).addEntity( objType );
            listOfData = objSQLQuery.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /**
     * Used to execute Native Query on given entity with maxRecord limit.
     *
     * @param strSqlQuery - native query to execute
     * @param objType - entity type
     * @param resultLimit - max records to be fetched
     * @return list of records fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public List<T> findByNativeQuery( String strSqlQuery, Class objType, int resultLimit )
            throws DatabaseException
    {

        String thisMethod = "findByNativeQuery";

        List<T> listOfData = null;
        try
        {
            SQLQuery objSQLQuery = factory.getCurrentSession().createSQLQuery( strSqlQuery ).addEntity( objType );
            if ( resultLimit > 0 )
            {
                objSQLQuery.setMaxResults( resultLimit );
            }
            listOfData = objSQLQuery.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /***
     * Used to execute Native Query on given entity . Values passed in a map are used as parameter for the query. used
     * for prepared statement
     *
     * @param strSqlQuery - native query to execute
     * @param objType - entity type
     * @param mapQueryParameters - key and values as parameter to query
     * @return list of records fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public List<T> findByNativeQuery( String strSqlQuery, Class objType, Map<String, Object> mapQueryParameters )
            throws DatabaseException
    {

        String thisMethod = "findByNativeQuery";

        List<T> listOfData = null;
        try
        {

            Set<Entry<String, Object>> setRawParameters = mapQueryParameters.entrySet();
            SQLQuery objSQLQuery = factory.getCurrentSession().createSQLQuery( strSqlQuery ).addEntity( objType );
            for ( Entry<String, Object> objEntry : setRawParameters )
            {
                objSQLQuery.setParameter( objEntry.getKey(), objEntry.getValue() );
            }
            listOfData = objSQLQuery.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /***
     * Used to execute Native Query on given entity . Values passed in a map are used as parameter for the query. No of
     * rows to be selected is specified by resultLimit. used for prepared statement
     *
     * @param strSqlQuery - native query to execute
     * @param objType - entity type
     * @param mapQueryParameters - key and values as parameter to query
     * @param resultLimit - rows to be selected
     * @return List of records fetched from database
     * @throws DatabaseException
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public List<T> findByNativeQuery( String strSqlQuery, Class objType, Map<String, Object> mapQueryParameters,
                                      int resultLimit )
            throws DatabaseException
    {

        String thisMethod = "findByNativeQuery";

        List<T> listOfData = null;
        try
        {

            Set<Entry<String, Object>> setRawParameters = mapQueryParameters.entrySet();
            SQLQuery objSQLQuery = factory.getCurrentSession().createSQLQuery( strSqlQuery ).addEntity( objType );
            if ( resultLimit > 0 )
            {
                objSQLQuery.setMaxResults( resultLimit );
            }
            for ( Entry<String, Object> objEntry : setRawParameters )
            {
                objSQLQuery.setParameter( objEntry.getKey(), objEntry.getValue() );
            }
            listOfData = objSQLQuery.list();
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return listOfData;
    }

    /**
     * This is used to create criteria on class specified by objPersistentClass
     *
     * @return criteria object on class specified by objPersistentClass
     * @throws DatabaseException
     */
    public Criteria createCriteria( Class<T> objPersistentClass )
            throws DatabaseException
    {

        String thisMethod = "createCriteria";

        Criteria criteria = null;
        try
        {
            criteria = getSession().createCriteria( objPersistentClass );
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return criteria;
    }

    /**
     * This method creates callableStatement using strSqlCallQuery
     *
     * @param strSqlCallQuery - String containing call statement to stored procedure or function
     * @return object of CallableStatement
     * @throws DatabaseException
     */
    public CallableStatement getCallableStatement( String strSqlCallQuery )
            throws DatabaseException
    {

        String thisMethod = "getCallableStatement";

        CallableStatement objCallableStatement = null;
        try
        {
            objCallableStatement =
                    (CallableStatement) ( (SessionImpl) getSession() ).connection().prepareCall( strSqlCallQuery );
        }
        catch ( HibernateException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "9007" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

        return objCallableStatement;
    }

    /**
     * This method takes sqlQuery in string format and creates PreparedStatement object.
     *
     * @return PreparedStatement object
     * @throws DatabaseException
     */
    @SuppressWarnings( "deprecation" )
    /**
     * Used to flush the session object
     *
     * @throws DatabaseException
     */
    public void flush()
            throws DatabaseException
    {
        String thisMethod = "flush";

        try
        {
            getSession().flush();
        }
        catch ( SessionException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "3024" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

    }

    /**
     * Used to clear the session object
     *
     * @throws DatabaseException
     */
    public void clear()
            throws DatabaseException
    {
        String thisMethod = "clear";

        try
        {
            getSession().clear();
        }
        catch ( SessionException e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            throw new DatabaseException( "3024" );
        }
        catch ( Exception e )
        {
            System.err.println(" Exception occured = " + e.getMessage());
            if ( e instanceof SQLException && "Too many connections".equalsIgnoreCase( e.getMessage() ) )
            {
                throw new DatabaseException( "9006" );
            }
            else
                throw new DatabaseException( "9003" );
        }

    }

    protected void addCriteriaIn( String propertyName, List<?> list, Criteria criteria )
    {
        String thisMethod = "addCriteriaIn";

        Disjunction or = Restrictions.disjunction();
        if ( list.size() > 1000 )
        {
            while ( list.size() > 1000 )
            {
                List<?> subList = list.subList( 0, 1000 );
                or.add( Restrictions.in( propertyName, subList ) );
                list.subList( 0, 1000 ).clear();
            }

        }

        or.add( Restrictions.in( propertyName, list ) );

        criteria.add( or );

    }
}