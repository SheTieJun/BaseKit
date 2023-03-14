

package shetj.me.base.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri


/**
 * 自定义ContentProvider的实现
 * ContentProvider主要用于在不同的应用程序之间共享数据,这也是官方推荐的方式.
 * 注意事项:
 * 1 在AndroidManifest.xml中注册ContentProvider时的属性
 *  android:exported="true"表示允许其他应用访问.
 * 2 注意*和#这两个符号在Uri中的作用
 *  其中*表示匹配任意长度的字符
 *  其中#表示匹配任意长度的数据
 *  所以：
 *  一个能匹配所有表的Uri可以写成:
 *  content://me.shetj.base.testcontentprovider/*
 *  一个能匹配person表中任意一行的Uri可以写成:
 *  content://me.shetj.base.testcontentprovider/person/#
 */*/


class TestContentProvider : ContentProvider() {


    private var mSQLiteDatabaseOpenHelper: SQLiteDatabaseOpenHelper? = null
    private val AUTHORITY = "me.shetj.base.testcontentprovider"
    private var mUriMatcher: UriMatcher? = null
    private val PERSON_DIR = 0
    private val PERSON = 1

    /**
     * 利用静态代码块初始化UriMatcher
     * 在UriMatcher中包含了多个Uri,每个Uri代表一种操作
     * 当调用UriMatcher.match(Uri uri)方法时就会返回该uri对应的code;
     * 比如此处的PERSONS和PERSON
     */
    init {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        // 该URI表示返回所有的person,其中PERSONS为该特定Uri的标识码
        mUriMatcher?.addURI(AUTHORITY, "person", PERSON_DIR)
        // 该URI表示返回某一个person,其中PERSON为该特定Uri的标识码
        mUriMatcher?.addURI(AUTHORITY, "person/#", PERSON)
    }

    override fun onCreate(): Boolean {
        mSQLiteDatabaseOpenHelper = SQLiteDatabaseOpenHelper(context)
        return true
    }


    /**
     * 查询操作:
     * 查询操作有两种可能:查询一张表或者查询某条数据
     *
     * 注意事项:
     * 在查询某条数据时要注意--因为此处是按照personid来查询
     * 某条数据,但是同时可能还有其他限制.例如:
     * 要求personid为2且name为xiaoming1
     * 所以在查询时分为两步:
     * 第一步:
     * 解析出personid放入where查询条件
     * 第二步:
     * 判断是否有其他限制(如name),若有则将其组拼到where查询条件.
     *
     * 详细代码见下.
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = mSQLiteDatabaseOpenHelper?.writableDatabase
        val cursor: Cursor?
        when (mUriMatcher?.match(uri)) {
            PERSON_DIR -> {
                // 查询表
                cursor = db!!.query(
                    "person",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            PERSON -> {
                // 按照id查询某条数据
                val id = ContentUris.parseId(uri)
                var where = "person=$id"
                if (selection != null && "" != selection.trim()) {
                    where = "$selection and $where"
                }
                cursor =
                    db?.query("person", projection, where, selectionArgs, null, null, sortOrder)
            }
            else -> {
                throw   IllegalArgumentException("unknown uri$uri")
            }
        }
        return cursor
    }

    /**
     * 在自定义ContentProvider中必须覆写getType(Uri uri)方法.
     * 该方法用于获取Uri对象所对应的MIME类型.
     *
     * 一个Uri对应的MIME字符串遵守以下三点:
     * 1 必须以vnd开头
     * 2 如果该Uri对应的数据可能包含多条记录,那么返回字符串应该以"vnd.android.cursor.dir/"开头
     * 3 如果该Uri对应的数据只包含一条记录,那么返回字符串应该以"vnd.android.cursor.item/"开头
     */
    override fun getType(uri: Uri): String {
        return when (mUriMatcher!!.match(uri)) {
            PERSON_DIR -> "vnd.android.cursor.dir/$AUTHORITY.persons"
            PERSON -> "vnd.android.cursor.item/$AUTHORITY.person"
            else -> throw IllegalArgumentException("unknown uri$uri")
        }
    }


    /**
     * private void testInsert(Person person) {
    ContentValues contentValues=new ContentValues();
    contentValues.put("name", person.getName());
    contentValues.put("phone", person.getPhone());
    contentValues.put("salary",person.getSalary());
    Uri insertUri=Uri.parse("content://me.shetj.base.testcontentprovider/person");
    Uri returnUri=mContentResolver.insert(insertUri, contentValues);
    System.out.println("新增数据:returnUri="+returnUri);
    }
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mSQLiteDatabaseOpenHelper!!.writableDatabase
        return when (mUriMatcher!!.match(uri)) {
            PERSON_DIR -> {
                val newId = db.insert("person", "name,phone,salary", values)
                //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
                context?.contentResolver?.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, newId)
            }
            else -> throw IllegalArgumentException("unknown uri$uri")
        }
    }

    /**
     * 删除操作:
     * 删除操作有两种可能:删除一张表或者删除某条数据
     * 在删除某条数据时原理类似于查询某条数据,见下.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = mSQLiteDatabaseOpenHelper!!.writableDatabase
        val deletedNum: Int
        when (mUriMatcher!!.match(uri)) {
            PERSON_DIR -> deletedNum = db.delete("person", selection, selectionArgs)
            PERSON -> {
                val id = ContentUris.parseId(uri)
                var where = "personid=$id"
                if (selection != null && "" != selection.trim()) {
                    where = "$selection and $where"
                }
                deletedNum = db.delete("person", where, selectionArgs)
            }
            else -> throw IllegalArgumentException("unknown uri$uri")
        }
        //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
        context!!.contentResolver.notifyChange(uri, null)
        return deletedNum
    }

    /**
     * 更新操作:
     * 更新操作有两种可能:更新一张表或者更新某条数据
     * 在更新某条数据时原理类似于查询某条数据,见下.
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = mSQLiteDatabaseOpenHelper?.writableDatabase
        val updatedNum: Int
        when (mUriMatcher?.match(uri)) {
            PERSON_DIR -> {
                updatedNum = db?.update("person", values, selection, selectionArgs)!!
            }
            PERSON -> {
                val id = ContentUris.parseId(uri)
                var where = "person=$id"
                if (selection != null && !"".equals(selection.trim())) {
                    where = "$selection and $where"
                }
                updatedNum = db?.update("person", values, where, selectionArgs)!!
            }
            else -> {
                throw IllegalArgumentException("unknown uri$uri")
            }

        }
        return updatedNum
    }
}