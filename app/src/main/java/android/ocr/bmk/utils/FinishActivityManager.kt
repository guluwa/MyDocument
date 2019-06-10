package android.ocr.bmk.utils

import android.app.Activity
import android.os.Process
import java.lang.ref.WeakReference
import java.util.*

/**i
 * 类描述：Activity管理类
 * 创建人： luwa
 * 创建时间： 2017/ic_num_seven/4 13:09
 */

class FinishActivityManager private constructor() {

    private var mActivityStack: Stack<WeakReference<Activity>>? = null

    /**
     * 添加Activity到栈
     *
     * @param activity
     */
    fun addActivity(activity: Activity) {
        if (mActivityStack == null) {
            mActivityStack = Stack()
        }
        mActivityStack!!.add(WeakReference(activity))
    }

    /**
     * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
     */
    fun checkWeakReference() {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                if (temp == null) {
                    it.remove()
                }
            }
        }
    }

    /**
     * 获取当前Activity（栈中最后一个压入的）
     *
     * @return
     */
    fun currentActivity(): Activity? {
        checkWeakReference()
        return if (mActivityStack != null && !mActivityStack!!.isEmpty()) {
            mActivityStack!!.lastElement().get()
        } else null
    }

    /**
     * 获取倒数第二个Activity（栈中最后一个压入的）
     *
     * @return
     */
    fun lastTwoActivity(): Activity? {
        checkWeakReference()
        return if (mActivityStack != null && !mActivityStack!!.isEmpty()) {
            mActivityStack!![mActivityStack!!.size - 2].get()
        } else null
    }

    /**
     * 关闭当前Activity（栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = currentActivity()
        if (activity != null) {
            finishActivity(activity)
        }
    }

    /**
     * 关闭指定的Activity
     *
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove()
                    continue
                }
                if (temp === activity) {
                    it.remove()
                }
            }
            activity.finish()
        }
    }

    /**
     * 获取指定的activity
     *
     * @param cls
     * @return
     */
    fun getActivity(cls: Class<*>): Activity? {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val activity = activityReference.get()
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove()
                    continue
                }
                if (activity.javaClass == cls) {
                    return activity
                }
            }
        }
        return null
    }

    /**
     * 关闭指定类名的所有Activity
     *
     * @param cls
     */
    fun finishActivity(cls: Class<*>) {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val activity = activityReference.get()
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove()
                    continue
                }
                if (activity.javaClass == cls) {
                    it.remove()
                    activity.finish()
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (mActivityStack != null) {
            for (activityReference in mActivityStack!!) {
                val activity = activityReference.get()
                activity?.finish()
            }
            mActivityStack!!.clear()
        }
    }

    /**
     * 结束所有Activity除了LoginActivity
     */
//    fun finishAllActivityExceptLogin() {
//        if (mActivityStack != null) {
//            for (activityReference in mActivityStack!!) {
//                val activity = activityReference.get()
//                if (activity != null && activity.javaClass != LoginActivity::class.java) {
//                    activity.finish()
//                }
//            }
//        }
//    }

    /**
     * 退出应用程序
     */
    fun exitApp() {
        try {
            finishAllActivity()
            // 退出JVM,释放所占内存资源,0表示正常退出
            System.exit(0)
            // 从系统中kill掉应用程序
            Process.killProcess(Process.myPid())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    object SingletonHolder {
        //单例（静态内部类）
        val instance = FinishActivityManager()
    }

    companion object {
        fun getInstance() = SingletonHolder.instance
    }
}
