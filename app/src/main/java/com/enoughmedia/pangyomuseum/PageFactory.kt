package com.enoughmedia.pangyomuseum
import android.content.pm.ActivityInfo
import com.enoughmedia.pangyomuseum.page.*
import com.enoughmedia.pangyomuseum.page.popup.*

import com.lib.page.PageFragment
import com.lib.page.PagePosition

class PageFactory {

    companion object {
        private var currentInstance: PageFactory? = null
        fun getInstance(): PageFactory {
            if (currentInstance == null) currentInstance = PageFactory()
            return currentInstance !!
        }

        fun getCategoryIdx(pageID: PageID):Int{
            return pageID.position.toString().first().toString().toInt()
        }
    }

    init {
        currentInstance = this
    }

    /**
     * 홈페이지 등록
     * 등록시 뒤로실행시 옙종료
     */
    val homePages: Array<PageID> = arrayOf(PageID.MAP)

    /**
     * 히스토리 사용안함
     * 등록시 뒤로실행시 패스
     */
    val disableHistoryPages: Array<PageID> = arrayOf(PageID.INTRO)

    /**
     * 재사용가능 페이지등록
     * 등록시 viewModel 및 fragment가 재사용 -> 페이지 재구성시 효율적
     */
    val backStackPages: Array<PageID> = arrayOf()


    private val fullScreenPage: Array<PageID> = arrayOf(PageID.POPUP_SCAN,
        PageID.INTRO, PageID.POPUP_GESTURE, PageID.MOUNDS, PageID.BOOK, PageID.POPUP_AR, PageID.POPUP_VR)

    fun isFullScreenPage(id: PageID): Boolean {
        return true
    }


    fun getPageOrientation(id: PageID): Int {
        return when (id) {
            PageID.MAP -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }



    fun getPageByID(id: PageID): PageFragment {
        return when (id) {
            PageID.INTRO -> PageIntro()
            PageID.MAP -> PageMap()
            PageID.MOUNDS -> PageMounds()
            PageID.BOOK -> PageBook()
            PageID.POPUP_AR -> PopupAR()
            PageID.POPUP_VR -> PopupVR()
            PageID.POPUP_SCAN -> PopupScan()
            PageID.POPUP_GUIDE -> PopupGuide()
            PageID.POPUP_GESTURE -> PopupGesture()
            PageID.POPUP_SETTING -> PopupSetting()
        }
    }
}

/**
 * PageID
 * position 값에따라 시작 에니메이션 변경
 * 기존페이지보다 클때 : 오른쪽 -> 왼족
 * 기존페이지보다 작을때 : 왼쪽 -> 오른쪽
 * history back 반대
 */
enum class PageID(val resId: Int, override var position: Int = 9999) : PagePosition {
    //group1
    INTRO(0,0),
    MAP(1, 100),
    MOUNDS(2, 200),
    BOOK(3, 300),

    POPUP_AR(1001),
    POPUP_VR(1002),
    POPUP_SCAN(1003),
    POPUP_GUIDE(1004),
    POPUP_GESTURE(1005),
    POPUP_SETTING(1006)
}