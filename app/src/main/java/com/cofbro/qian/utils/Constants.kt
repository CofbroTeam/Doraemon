package com.cofbro.qian.utils

object Constants {
    /**
     * 签到类型
     *
     * {
     *     "activeSort": 6,
     *     "activeType": 2,
     *     "activityTranMode": 0,
     *     "attendNum": 0,
     *     "bsid": 0,
     *     "chartId": "227574608363521",
     *     "clazzId": 83807809,
     *     "configJson": "{\"sendToOtherClassList\":\"\",\"ifSendToOtherClass\":0}",
     *     "courseId": "",
     *     "createTime": {
     *         "date": 7,
     *         "day": 6,
     *         "hours": 15,
     *         "minutes": 3,
     *         "month": 9,
     *         "nanos": 0,
     *         "seconds": 31,
     *         "time": 1696662211000,
     *         "timezoneOffset": -480,
     *         "year": 123
     *     },
     *     "createUid": "191970731",
     *     "createxxuid": "191970731",
     *     "credit": "",
     *     "currentVersion": 0,
     *     "currentstatus": 0,
     *     "dpurl": "",
     *     "editconfig": "",
     *     "endTime": {
     *         "date": 7,
     *         "day": 6,
     *         "hours": 15,
     *         "minutes": 33,
     *         "month": 9,
     *         "seconds": 31,
     *         "time": 1696664011000,
     *         "timezoneOffset": -480,
     *         "year": 123
     *     },
     *     "endtimes": "",
     *     "groupType": 0,
     *     "id": 2000072590142,
     *     "ifGetRange": 0,
     *     "ifPhoto": 0,
     *     "ifRefreshEwm": 0,
     *     "ifSendMessage": 0,
     *     "ifaveraged": 0,
     *     "ifcommit": 0,
     *     "iphoneContent": "",
     *     "isAnony": 0,
     *     "isBegins": 0,
     *     "isClone": 1,
     *     "isDelete": 0,
     *     "isResult": 0,
     *     "isbackfill": 0,
     *     "isnorm": 0,
     *     "isold": 0,
     *     "jurl": "",
     *     "jwCourseId": "",
     *     "latitude": 0,
     *     "longitude": 0,
     *     "name": "签到",
     *     "newOld": 0,
     *     "normScore": "",
     *     "otherId": 0,
     *     "parentId": 2000072590141,
     *     "pptPlanId": 2000004738914,
     *     "pptnum": 0,
     *     "releaseNum": 0,
     *     "setEndTimeNull": 0,
     *     "sfdp": 0,
     *     "sffxs": 0,
     *     "showhide": 0,
     *     "source": 15,
     *     "startTime": {
     *         "date": 7,
     *         "day": 6,
     *         "hours": 15,
     *         "minutes": 3,
     *         "month": 9,
     *         "seconds": 31,
     *         "time": 1696662211000,
     *         "timezoneOffset": -480,
     *         "year": 123
     *     },
     *     "starttimes": "",
     *     "status": 1,
     *     "sxs": 0,
     *     "timeLong": 1800000,
     *     "updateTime": null,
     *     "url": "",
     *     "viewPicPath": "https://mobilelearn.chaoxing.com/front/mobile/common/images/teachingPlan/ppt_qd.png",
     *     "zhjsid": 0
     * }
     *
     */
    object SIGN {
        // 普通签到(包含图片签到)
        const val NORMAl = "0"

        // 细分是否图片签到
        const val PHOTO = "1"

        // 扫码签到
        const val SCAN_QR = "2"

        // 手势签到
        const val GESTURE = "3"

        // 定位签到
        const val LOCATION = "4"

        // 签到码签到
        const val SIGN_CODE = "5"


        const val ID = "id"
        const val IF_PHOTO = "ifPhoto"
        const val OTHER_ID = "otherId"
    }

    /**
     * 签到活动状态
     */
    object STATUS {
        //const val OPEN = "1"
        const val CLOSE = "2"
    }

    // 活动类型
    object ACTIVITY {
        const val SIGN = "2"
    }

    /**
     * 课程列表
     * {
     *     "result": 1,
     *     "msg": "获取成功",
     *     "channelList": [
     *         {
     *             "cfid": -1,
     *             "catalogId": 1938504,
     *             "cataid": "100000017",
     *             "key": 1938504,
     *             "content": {
     *                 "cfid": 1938504,
     *                 "uid": "191970731",
     *                 "createtime": 1634721241000,
     *                 "puid": "191970731",
     *                 "pid": 0,
     *                 "folderName": "",
     *                 "cpi": 197696045,
     *                 "shareType": 10
     *             }
     *         },
     *         {
     *             "cfid": -1,
     *             "norder": 13492163,
     *             "cataName": "课程",
     *             "cataid": "100000002",
     *             "id": 0,
     *             "cpi": 197696045,
     *             "key": 81725723,
     *             "content": {
     *                 "studentcount": 61,
     *                 "chatid": "225441846722564",
     *                 "isFiled": 0,
     *                 "isthirdaq": 0,
     *                 "isstart": true,
     *                 "isretire": -1,
     *                 "name": "信管3、4（实验）",
     *                 "course": {
     *                     "data": [
     *                         {
     *                             "belongSchoolId": "1840",
     *                             "coursestate": 0,
     *                             "teacherfactor": "黄煜",
     *                             "isCourseSquare": 0,
     *                             "schools": "西南大学",
     *                             "courseSquareUrl": "https://tsjy.chaoxing.com/plaza/app?courseId=236709512&personId=197696045&classId=81725723&userId=191970731",
     *                             "imageurl": "https://p.ananas.chaoxing.com/star3/origin/6ce77a10dd3268daa7ba6c93e5e76459.jpg",
     *                             "appInfo": "4",
     *                             "name": "商务智能与数据挖掘",
     *                             "defaultShowCatalog": 0,
     *                             "id": 236709512,
     *                             "appData": 0
     *                         }
     *                     ]
     *                 },
     *                 "roletype": 3,
     *                 "id": 81725723,
     *                 "state": 0,
     *                 "cpi": 197696045,
     *                 "bbsid": "88510f58f5f7d4ded6846312ddb079ba",
     *                 "isSquare": 0
     *             },
     *             "topsign": 0
     *         }
     *     ]
     *     "mcode": "-1",
     *     "createcourse": 1,
     *     "teacherEndCourse": 0,
     *     "showEndCourse": 1,
     *     "hasMore": false,
     *     "stuEndCourse": 1
     * }
     */
    object CourseList {
        const val CATA_NAME = "cataName"
        const val CHANNEL_LIST = "channelList"
        const val CPI = "cpi"
        const val KEY = "key"
        const val STUDENT_COUNT = "content.studentcount"
        const val CLASS_NAME = "content.name"
        const val DATA = "content.course.data"
        const val COURSE_ID = "id"
        const val COURSE_NAME = "name"
        const val SCHOOLS = "schools"
        const val TEACHER_NAME = "teacherfactor"
        const val IMG_URL = "imageurl"
    }

    /**
     * 活动列表
     * {
     *     "groupList": [
     *         {
     *             "classId": "",
     *             "content": "",
     *             "courseId": "",
     *             "createTime": null,
     *             "fid": "",
     *             "id": 1,
     *             "isDelete": 0,
     *             "name": "进行中(1)",
     *             "sort": 0,
     *             "type": 0,
     *             "uid": "",
     *             "updateTime": null
     *         },
     *         {
     *             "classId": "",
     *             "content": "",
     *             "courseId": "",
     *             "createTime": null,
     *             "fid": "",
     *             "id": 0,
     *             "isDelete": 0,
     *             "name": "未开始(0)",
     *             "sort": 0,
     *             "type": 0,
     *             "uid": "",
     *             "updateTime": null
     *         },
     *         {
     *             "classId": "",
     *             "content": "",
     *             "courseId": "",
     *             "createTime": null,
     *             "fid": "",
     *             "id": 2,
     *             "isDelete": 0,
     *             "name": "已结束(1)",
     *             "sort": 0,
     *             "type": 0,
     *             "uid": "",
     *             "updateTime": null
     *         }
     *     ],
     *     "activeList": [
     *         {
     *             "nameTwo": "结束时间：09-11 14:49",
     *             "groupId": 2,
     *             "isLook": 1,
     *             "releaseNum": 0,
     *             "url": "https://mobilelearn.chaoxing.com/newsign/preSign?courseId=236709512&classId=81725723&activePrimaryId=1000070282247&general=1&sys=1&ls=1&appType=15&uid=191970731&isTeacherViewOpen=0",
     *             "picUrl": "https://mobilelearn.chaoxing.com/front/mobile/common/images/newActiveIcon80/active_type_2_gray.png?v=4",
     *             "attendNum": 0,
     *             "activeType": 2,
     *             "nameOne": "二维码签到",
     *             "startTime": 1694413183000,
     *             "id": 1000070282247,
     *             "status": 2,
     *             "nameFour": ""
     *         }
     *     ],
     *     "count": 1,
     *     "status": 1,
     *     "result": 1,
     *     "ext": {
     *         "_from_": "236709512_81725723_191970731_0504d7d5602cd2208f473afc97d0d026"
     *     }
     * }
     */
    object TaskList {
        const val ACTIVE_LIST = "activeList"
        // 活动图片
        const val PIC_URL = "picUrl"
        // 活动名称
        const val TITLE = "nameOne"
        // 活动截止时间
        const val TIME_LINE = "nameTwo"
        // 活动数量
        const val COUNT = "count"
        // aid
        const val ID = "id"
        // 活动类型
        const val ACTIVE_TYPE = "activeType"
        // 预签到url
        const val PRE_SIGN_URL = "url"
        // 活动状态
        const val STATUS = "status"
    }

    /**
     * 图片上传
     * {
     *     "result": true,
     *     "msg": "success",
     *     "crc": "619dd4fdd9045d08cb7795c64817684d",
     *     "objectId": "45cb143b5af5dc4b3c614d51b1b3f550",
     *     "resid": 918995675576205312,
     *     "puid": 191970813,
     *     "data": {
     *         "disableOpt": false,
     *         "resid": 918995675576205312,
     *         "crc": "619dd4fdd9045d08cb7795c64817684d",
     *         "puid": 191970813,
     *         "isfile": true,
     *         "pantype": "USER_PAN",
     *         "size": 3353846,
     *         "name": "1697035241106.jpg",
     *         "objectId": "45cb143b5af5dc4b3c614d51b1b3f550",
     *         "restype": "RES_TYPE_NORMAL",
     *         "uploadDate": "2023-10-11T22:40:43.048+08:00",
     *         "modifyDate": "2023-10-11T22:40:43.048+08:00",
     *         "uploadDateFormat": "2023-10-11",
     *         "residstr": "918995675576205312",
     *         "suffix": "jpg",
     *         "preview": "https://pan-yz.chaoxing.com/preview/showpreview_918995675576205312.html?v=1697035243196&enc=a568e8d30a7403556317116885a86616",
     *         "thumbnail": "https://pan-yz.chaoxing.com/thumbnail/origin/45cb143b5af5dc4b3c614d51b1b3f550?type=img",
     *         "creator": 191970813,
     *         "duration": 0,
     *         "isImg": true,
     *         "isOffice": false,
     *         "previewUrl": "https://p.ananas.chaoxing.com/star3/origin/45cb143b5af5dc4b3c614d51b1b3f550.jpg",
     *         "filetype": "",
     *         "filepath": "",
     *         "sort": 0,
     *         "topsort": 0,
     *         "resTypeValue": 3,
     *         "extinfo": ""
     *     }
     * }
     *
     */
    object Upload {
        // 上传图片的token
        const val TOKEN = "_token"
        // 图片唯一标识
        const val OBJECT_ID = "objectId"
    }

    object RecycleJson {
        const val HOME_JSON_DATA = "home_json_data"
        const val ACCOUNT_JSON_DATA = "account_data"
    }


    object DataLoad {
        const val FIRST_LOAD = "first_load"
        const val UNLOAD = "unload"
        const val LOADED = "loaded"
    }

    object Work {
        const val SUBMIT_URL = "url"
        const val PREFIX_URL = "prefixPostUrl"
        const val DESCRIPTION = "description"
        const val WORK_TYPE = "workType"
        const val TITLE = "title"
        const val OBJECT_ID = "objectid"
    }

    object Account {
        const val UID = "uid"
        const val FID = "fid"
        const val COOKIE = "cookie"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val PIC_URL = "picUrl"
        const val USERS = "users"
        const val SIZE = "size"
        const val HISTORY = "history"
    }

    object Recorder {
        const val FILE_NAME = "sign_record"
        const val UID = "uid"
        const val COURSE_NAME = "courseName"
        const val TIME = "time"
        const val STATUS = "status"
        const val SIZE = "size"
        const val RECORDS = "records"
    }

    object USER {
        const val USERNAME = "username"
        const val UID = "uid"
    }
}