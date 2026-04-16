package hcmute.edu.vn.smartstudyassistant.domain.usecase.dashboard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DailyTipEntity
import hcmute.edu.vn.smartstudyassistant.data.local.dao.DailyTipDao
import hcmute.edu.vn.smartstudyassistant.domain.repository.PomodoroRepository
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.Constants
import hcmute.edu.vn.smartstudyassistant.util.daysFromNow
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class GenerateDailyTipUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val flashcardRepository: FlashcardRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val gamificationRepository: GamificationRepository,
    private val dailyTipDao: DailyTipDao
) {
    private val generalTips = listOf(
        "Hãy ôn tập định kỳ thay vì học dồn vào một lúc.",
        "Chia nhỏ mục tiêu lớn thành các bước nhỏ để dễ thực hiện hơn.",
        "Nghỉ ngơi đúng lúc giúp não bộ hấp thụ thông tin tốt hơn.",
        "Dạy lại cho người khác là cách học hiệu quả nhất.",
        "Ghi chú bằng tay giúp ghi nhớ lâu hơn so với gõ máy tính.",
        "Tạo liên kết giữa kiến thức mới và những gì bạn đã biết.",
        "Học vào buổi sáng sớm khi não bộ còn tươi táo.",
        "Tránh đa nhiệm khi học - tập trung vào một việc tại một thời điểm.",
        "Uống đủ nước - não bộ cần nước để hoạt động hiệu quả.",
        "Ngủ đủ giấc để củng cố ký ức và thông tin đã học."
    )

    suspend operator fun invoke(userId: Long): String {
        val today = todayMillis()

        // Return cached tip if exists
        dailyTipDao.getByDate(userId, today)?.let { return it.tipText }

        val now = System.currentTimeMillis()
        val tip = generateTip(userId, now, today)

        // Persist the tip
        dailyTipDao.insertOrUpdate(
            DailyTipEntity(userId = userId, tipText = tip, tipCategory = "auto", date = today)
        )
        return tip
    }

    private suspend fun generateTip(userId: Long, now: Long, today: Long): String {
        val overdueCount = taskRepository.getOverdueCount(userId, now)
        if (overdueCount > Constants.TIP_OVERDUE_THRESHOLD) {
            return "Bạn có $overdueCount task quá hạn. Hãy ưu tiên xử lý chúng trước!"
        }

        val cardsDue = flashcardRepository.getDueCardsList(
            deckId = 0, today = today
        ).size // Simplified — use total due across all decks
        if (cardsDue > Constants.TIP_CARDS_DUE_THRESHOLD) {
            return "Có $cardsDue thẻ cần ôn tập. Spaced repetition giúp nhớ lâu hơn 50%!"
        }

        val yesterdayStart = today - 24 * 60 * 60 * 1000L
        val focusYesterday = pomodoroRepository.getFocusMinutesByDate(userId, yesterdayStart, today)
        if (focusYesterday < Constants.TIP_FOCUS_YESTERDAY_THRESHOLD_MINUTES) {
            return "Hãy thử 1 phiên Pomodoro 25 phút để bắt đầu ngày học tập hiệu quả!"
        }

        val streak = gamificationRepository.getLatestStreak(userId)?.currentStreak ?: 0
        if (streak > Constants.TIP_STREAK_THRESHOLD) {
            return "Tuyệt vời! Chuỗi $streak ngày liên tục. Tiếp tục phát huy nhé!"
        }

        return generalTips.random()
    }
}
