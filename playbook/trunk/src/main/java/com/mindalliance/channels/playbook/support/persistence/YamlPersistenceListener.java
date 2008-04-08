package com.mindalliance.channels.playbook.support.persistence;

import com.opensymphony.oscache.base.persistence.PersistenceListener;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import com.mindalliance.channels.playbook.ref.Bean;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.mem.ApplicationMemory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ho.yaml.Yaml;

import java.io.*;

import java.util.Set;
import java.util.Map;

import javax.servlet.jsp.PageContext;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * This class is mostly copied from com.opensymphony.oscache.plugins.diskpersistence.AbstractDiskPersistenceListener which
 * unfortunately was not properly abstracted.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2008
 * Time: 2:11:04 PM
 *
 * This is a quick hack on class AbstractDiskPersistenceListener to persist IFM POGOs using JYAML.
 * It ought to be refactored using YAML object wrappers that handle OSCache CacheEntry elements containing IFM POGOs. 
 *
 */
class YamlPersistenceListener implements PersistenceListener, Serializable {
    public final static String CACHE_PATH_KEY = "cache.path";

    /**
     * File extension for disk cache file
     */
    protected final static String CACHE_EXTENSION = "yaml";

    /**
     * The directory that cache groups are stored under
     */
    protected final static String GROUP_DIRECTORY = "__groups__";

    /**
     * Sub path name for application cache
     */
    protected final static String APPLICATION_CACHE_SUBPATH = "application";

    /**
     * Sub path name for session cache
     */
    protected final static String SESSION_CACHE_SUBPATH = "session";

    /**
     * Property to get the temporary working directory of the servlet container.
     */
    protected static final String CONTEXT_TMPDIR = "javax.servlet.context.tempdir";
    private static transient final Log log = LogFactory.getLog(YamlPersistenceListener.class);

    private static final String CHARS_TO_CONVERT = "./\\ :;\"\'_?";

    /**
     * Base path where the disk cache reside.
     */
    private File cachePath = null;
    private File contextTmpDir;

    /**
     * Root path for disk cache
     */
    private String root = null;

    /**
     * Get the physical cache path on disk.
     *
     * @return A file representing the physical cache location.
     */
    public File getCachePath() {
        return cachePath;
    }

    /**
     * Get the root directory for persisting the cache on disk.
     * This path includes scope and sessionId, if any.
     *
     * @return A String representing the root directory.
     */
    public String getRoot() {
        return root;
    }

    /**
     * Get the servlet context tmp directory.
     *
     * @return A file representing the servlet context tmp directory.
     */
    public File getContextTmpDir() {
        return contextTmpDir;
    }

    /**
     * Verify if a group exists in the cache
     *
     * @param group The group name to check
     * @return True if it exists
     * @throws CachePersistenceException
     */
    public boolean isGroupStored(String group) throws CachePersistenceException {
        try {
            File file = getCacheGroupFile(group);

            return file.exists();
        } catch (Exception e) {
            throw new CachePersistenceException("Unable verify group '" + group + "' exists in the cache: " + e);
        }
    }

    /**
     * Verify if an object is currently stored in the cache
     *
     * @param key The object key
     * @return True if it exists
     * @throws CachePersistenceException
     */
    public boolean isStored(String key) throws CachePersistenceException {
        try {
            File file = getCacheFile(key);

            return file.exists();
        } catch (Exception e) {
            throw new CachePersistenceException("Unable verify id '" + key + "' is stored in the cache: " + e);
        }
    }

    /**
     * Clears the whole cache directory, starting from the root
     *
     * @throws CachePersistenceException
     */
    public void clear() throws CachePersistenceException {
        clear(root);
    }

    /**
     * Initialises this <tt>YamlPersistenceListener</tt> using the supplied
     * configuration.
     *
     * @param config The OSCache configuration
     */
    public PersistenceListener configure(Config config) {
        String sessionId = null;
        int scope = 0;
        initFileCaching(config.getProperty(CACHE_PATH_KEY));

        if (config.getProperty(ServletCacheAdministrator.HASH_KEY_SESSION_ID) != null) {
            sessionId = config.getProperty(ServletCacheAdministrator.HASH_KEY_SESSION_ID);
        }

        if (config.getProperty(ServletCacheAdministrator.HASH_KEY_SCOPE) != null) {
            scope = Integer.parseInt(config.getProperty(ServletCacheAdministrator.HASH_KEY_SCOPE));
        }

        StringBuffer buf = new StringBuffer(getCachePath().getPath());
        buf.append("/");
        buf.append(getPathPart(scope));

        if ((sessionId != null) && (sessionId.length() > 0)) {
            buf.append("/");
            buf.append(sessionId);
        }

        this.root = buf.toString();
        this.contextTmpDir = (File) config.get(ServletCacheAdministrator.HASH_KEY_CONTEXT_TMPDIR);

        return this;
    }

    /**
     * Delete a single cache entry.
     *
     * @param key The object key to delete
     * @throws CachePersistenceException
     */
    public void remove(String key) throws CachePersistenceException {
        File file = getCacheFile(key);
        remove(file);
    }

    /**
     * Deletes an entire group from the cache.
     *
     * @param groupName The name of the group to delete
     * @throws CachePersistenceException
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        File file = getCacheGroupFile(groupName);
        remove(file);
    }

    /**
     * Retrieve an object from the disk
     *
     * @param key The object key
     * @return The retrieved object
     * @throws CachePersistenceException
     */
    public Object retrieve(String key) throws CachePersistenceException {
        return retrieve(getCacheFile(key));
    }

    /**
     * Retrieves a group from the cache, or <code>null</code> if the group
     * file could not be found.
     *
     * @param groupName The name of the group to retrieve.
     * @return A <code>Set</code> containing keys of all of the cache
     *         entries that belong to this group.
     * @throws CachePersistenceException
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        File groupFile = getCacheGroupFile(groupName);

        try {
            return (Set) retrieve(groupFile);
        } catch (ClassCastException e) {
            throw new CachePersistenceException("Group file " + groupFile + " was not persisted as a Set: " + e);
        }
    }

    /**
     * Stores an object in cache
     *
     * @param key The object's key
     * @param obj The object to store
     * @throws CachePersistenceException
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        File file = getCacheFile(key);
        store(file, obj);
    }

    /**
     * Stores a group in the persistent cache. This will overwrite any existing
     * group with the same name
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        File groupFile = getCacheGroupFile(groupName);
        store(groupFile, group);
    }

    /**
     * Allows to translate to the temp dir of the servlet container if cachePathStr
     * is javax.servlet.context.tempdir.
     *
     * @param cachePathStr Cache path read from the properties file.
     * @return Adjusted cache path
     */
    protected String adjustFileCachePath(String cachePathStr) {
        if (cachePathStr.compareToIgnoreCase(CONTEXT_TMPDIR) == 0) {
            cachePathStr = contextTmpDir.getAbsolutePath();
        }

        return cachePathStr;
    }

    /**
     * Set caching to file on or off.
     * If the <code>cache.path</code> property exists, we assume file caching is turned on.
     * By the same token, to turn off file caching just remove this property.
     */
    protected void initFileCaching(String cachePathStr) {
        if (cachePathStr != null) {
            cachePath = new File(cachePathStr);

            try {
                if (!cachePath.exists()) {
                    if (log.isInfoEnabled()) {
                        log.info("cache.path '" + cachePathStr + "' does not exist, creating");
                    }

                    cachePath.mkdirs();
                }

                if (!cachePath.isDirectory()) {
                    log.error("cache.path '" + cachePathStr + "' is not a directory");
                    cachePath = null;
                } else if (!cachePath.canWrite()) {
                    log.error("cache.path '" + cachePathStr + "' is not a writable location");
                    cachePath = null;
                }
            } catch (Exception e) {
                log.error("cache.path '" + cachePathStr + "' could not be used", e);
                cachePath = null;
            }
        } else {
            // Use default value
        }
    }

    // try 30s to delete the file
    private static final long DELETE_THREAD_SLEEP = 500;
    private static final int DELETE_COUNT = 60;

    protected void remove(File file) throws CachePersistenceException {
        int count = DELETE_COUNT;
        try {
            // Loop until we are able to delete (No current read).
            // The cache must ensure that there are never two concurrent threads
            // doing write (store and delete) operations on the same item.
            // Delete only should be enough but file.exists prevents infinite loop
            while (file.exists() && !file.delete() && count != 0) {
                count--;
                try {
                    Thread.sleep(DELETE_THREAD_SLEEP);
                } catch (InterruptedException ignore) {
                }
            }
        } catch (Exception e) {
            throw new CachePersistenceException("Unable to remove '" + file + "' from the cache: " + e);
        }
        if (file.exists() && count == 0) {
            throw new CachePersistenceException("Unable to delete '" + file + "' from the cache. " + DELETE_COUNT + " attempts at " + DELETE_THREAD_SLEEP + " milliseconds intervals.");
        }
    }

    /**
     * Stores an object using the supplied file object
     *
     * @param file The file to use for storing the object
     * @param obj  the object to store
     * @throws CachePersistenceException
     */
    protected void store(File file, Object obj) throws CachePersistenceException {
        // check if file exists before testing if parent exists
        if (!file.exists()) {
            // check if the directory structure required exists and create it if it doesn't
            File filepath = new File(file.getParent());

            try {
                if (!filepath.exists()) {
                    filepath.mkdirs();
                }
            } catch (Exception e) {
                throw new CachePersistenceException("Unable to create the directory " + filepath);
            }
        }

        // Write the object to disk
        Object object = obj;
        try {
            if (object instanceof CacheEntry) {
                CacheEntry ce = (CacheEntry) object;
                object = new CacheEntryBean(ce);
                Object content = ce.getContent();
                if (content instanceof Bean) {
                    Bean bean = (Bean) content;
                    Map propMap = bean.toMap();
                    ((CacheEntryBean) object).setContent(propMap); // Includes key _bean_class_ with value the name of class implementing Bean
                }
            }
            Yaml.dump(object, file);
        } catch (Exception e) {
            int count = DELETE_COUNT;
            while (file.exists() && !file.delete() && count != 0) {
                count--;
                try {
                    Thread.sleep(DELETE_THREAD_SLEEP);
                } catch (InterruptedException ignore) {
                }
            }
            throw new CachePersistenceException("Unable to write '" + file + "' in the cache. Exception: " + e.getClass().getName() + ", Message: " + e.getMessage());
        }
    }

    /**
     * Build fully qualified cache file for the specified cache entry key.
     *
     * @param key Cache Entry Key.
     * @return File reference.
     */
    protected File getCacheFile(String key) {
        char[] fileChars = getCacheFileName(key);

        File file = new File(root, new String(fileChars) + "." + CACHE_EXTENSION);

        return file;
    }

    /**
     * Build cache file name for the specified cache entry key.
     *
     * @param key Cache Entry Key.
     * @return char[] file name.
     */
    protected char[] getCacheFileName(String key) {
        if ((key == null) || (key.length() == 0)) {
            throw new IllegalArgumentException("Invalid key '" + key + "' specified to getCacheFile.");
        }

        char[] chars = key.toCharArray();

        StringBuffer sb = new StringBuffer(chars.length + 8);

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            int pos = CHARS_TO_CONVERT.indexOf(c);

            if (pos >= 0) {
                sb.append('_');
                sb.append(i);
            } else {
                sb.append(c);
            }
        }

        char[] fileChars = new char[sb.length()];
        sb.getChars(0, fileChars.length, fileChars, 0);
        return fileChars;
    }

    /**
     * Builds a fully qualified file name that specifies a cache group entry.
     *
     * @param group The name of the group
     * @return A File reference
     */
    private File getCacheGroupFile(String group) {
        int AVERAGE_PATH_LENGTH = 30;

        if ((group == null) || (group.length() == 0)) {
            throw new IllegalArgumentException("Invalid group '" + group + "' specified to getCacheGroupFile.");
        }

        StringBuffer path = new StringBuffer(AVERAGE_PATH_LENGTH);

        // Build a fully qualified file name for this group
        path.append(GROUP_DIRECTORY).append('/');
        path.append(getCacheFileName(group)).append('.').append(CACHE_EXTENSION);

        return new File(root, path.toString());
    }

    /**
     * This allows to persist different scopes in different path in the case of
     * file caching.
     *
     * @param scope Cache scope.
     * @return The scope subpath
     */
    private String getPathPart(int scope) {
        if (scope == PageContext.SESSION_SCOPE) {
            return SESSION_CACHE_SUBPATH;
        } else {
            return APPLICATION_CACHE_SUBPATH;
        }
    }

    /**
     * Clears a whole directory, starting from the specified
     * directory
     *
     * @param baseDirName The root directory to delete
     * @throws CachePersistenceException
     */
    private void clear(String baseDirName) throws CachePersistenceException {
        File baseDir = new File(baseDirName);
        File[] fileList = baseDir.listFiles();

        try {
            if (fileList != null) {
                // Loop through all the files and directory to delete them
                for (int count = 0; count < fileList.length; count++) {
                    if (fileList[count].isFile()) {
                        fileList[count].delete();
                    } else {
                        // Make a recursive call to delete the directory
                        clear(fileList[count].toString());
                        fileList[count].delete();
                    }
                }
            }

            // Delete the root directory
            baseDir.delete();
        } catch (Exception e) {
            throw new CachePersistenceException("Unable to clear the cache directory");
        }
    }

    /**
     * Retrives a serialized object from the supplied file, or returns
     * <code>null</code> if the file does not exist.
     *
     * @param file The file to deserialize
     * @return The deserialized object
     * @throws CachePersistenceException
     */
    private Object retrieve(File file) throws CachePersistenceException {
        Object readContent = null;
        boolean fileExist;

        try {
            fileExist = file.exists();
        } catch (Exception e) {
            throw new CachePersistenceException("Unable to verify if " + file + " exists: " + e);
        }

        // Read the file if it exists
        if (fileExist) {
            try {
                readContent = Yaml.load(file);
                if (readContent instanceof CacheEntryBean) {
                    readContent = ((CacheEntryBean) readContent).toCacheEntry();
                    Object content = ((CacheEntry) readContent).getContent();
                    if (content instanceof Map) {
                        Map mapContent = (Map) content;
                        if (mapContent.containsKey(CacheEntryBean.CLASS_NAME_KEY)) {
                            Bean bean = BeanImpl.fromMap(mapContent);
                            ((CacheEntry) readContent).setContent(bean);
                        }
                    }
                }
            } catch (Exception e) {
                // We expect this exception to occur.
                // This is when the item will be invalidated (written or deleted)
                // during read.
                // The cache has the logic to retry reading.
                throw new CachePersistenceException("Unable to read '" + file.getAbsolutePath() + "' from the cache: " + e);
            }
        }

        return readContent;
    }

}