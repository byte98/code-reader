package cz.skodaj.codereader.model.db

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import cz.skodaj.codereader.model.services.CodeService
import cz.skodaj.codereader.model.services.FolderService
import kotlin.reflect.KClass

/**
 * Class which creates different connections to the database.
 */
class DatabaseFactory {

    /**
     * List of all created connections.
     */
    private val connections: MutableList<SQLiteOpenHelper> = mutableListOf()

    /**
     * Context of the application.
     */
    private val context: Context

    /**
     * Creates new factory of connections to the database.
     * @param context Actual context of the application.
     * @param source Class which requested creation of factory.
     */
    public constructor(context: Context, source: KClass<*>){
        this.context = context
        DatabaseFactory.addFactory(source, this)
    }

    /**
     * Creates new connection to the table with folders.
     * @return Service which handles communication with table with folders.
     */
    public fun folders(): FolderService{
        val db: FolderDatabaseHelper = FolderDatabaseHelper(this.context)
        this.connections.add(db)
        return FolderService(db)
    }

    /**
     * Creates new connection to the table with codes.
     * @return Service which handles communication with table with codes.
     */
    public fun codes(): CodeService{
        val db: CodeDatabaseHelper = CodeDatabaseHelper(this.context)
        this.connections.add(db)
        return CodeService(db, this.folders())
    }

    /**
     * Closes all created connections.
     */
    public fun close(){
        for (connection in this.connections){
            connection.close()
        }
        this.connections.clear()
    }

    companion object{

        /**
         * Map of all created factories by any source.
         */
        private val factories: MutableMap<KClass<*>, MutableList<DatabaseFactory>> = mutableMapOf()

        /**
         * Adds factory to map of created factories.
         */
        private fun addFactory(cls: KClass<*>, factory: DatabaseFactory){
            if (DatabaseFactory.factories.containsKey(cls) == false){
                DatabaseFactory.factories.put(cls, mutableListOf())
            }
            DatabaseFactory.factories.get(cls)?.add(factory)
        }

        /**
         * Closes all connections opened for specified source.
         * @param source Class which requested opening of the connection and which connection will be closed.
         */
        public fun closeForSource(source: KClass<*>){
            if (DatabaseFactory.factories.containsKey(source)){
                for (factory in DatabaseFactory.factories.get(source) ?: emptyList<DatabaseFactory>()){
                    factory.close()
                }
                DatabaseFactory.factories.get(source)?.clear()
            }
        }

        /**
         * Closes all opened connections.
         */
        public fun closeAll() {
            for(source in DatabaseFactory.factories.keys){
                DatabaseFactory.closeForSource(source)
            }
        }

    }
}