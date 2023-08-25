package shetj.me.base.day

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.*
import shetj.me.base.databinding.LMtrlCalendarDayBinding

class MonthAdapter(private val month: Month) : RecyclerView.Adapter<BaseViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
         return BaseViewHolder(LMtrlCalendarDayBinding.inflate(LayoutInflater.from(parent.context),parent,false).root)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val offsetPosition: Int = position - firstPositionInMonth()
        var dayNumber = NO_DAY_NUMBER
        if (offsetPosition < 0 || offsetPosition >= month.daysInMonth) {
            holder.itemView.visibility = View.GONE
            holder.itemView.isEnabled = false
        } else {
            dayNumber = offsetPosition + 1
            // The tag and text uniquely identify the view within the MaterialCalendar for testing
            holder.itemView.tag = month
            val locale = holder.itemView.resources.configuration.locale
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.isEnabled = true
            (holder.itemView as TextView).text = String.format(locale, "%d", dayNumber)
        }
    }

    override fun getItemId(position: Int): Long {
        return (position / month.daysInWeek).toLong()
    }


    private fun isToday(date: Long): Boolean {
        return LocalDates.getTodayCalendar().timeInMillis == date
    }

    override fun getItemCount(): Int {
        return dayToPosition(month.daysInMonth)+1
    }
    /**
     * Returns the index of the first position which is part of the month.
     *
     *
     * For example, this returns the position index representing February 1st. Since position 0
     * represents a day which must be the first day of the week, the first position in the month may
     * be greater than 0.
     */
    fun firstPositionInMonth(): Int {
        //Calendar.MONDAY
        return month.daysFromStartOfWeekToFirstOfMonth(Calendar.MONDAY)
    }

    /**
     * Returns the index of the last position which is part of the month.
     *
     *
     * For example, this returns the position index representing November 30th. Since position 0
     * represents a day which must be the first day of the week, the last position in the month may
     * not match the number of days in the month.
     */
    fun lastPositionInMonth(): Int {
        return firstPositionInMonth() + month.daysInMonth - 1
    }

    /**
     * Returns the day representing the provided adapter index
     *
     * @param position The adapter index
     * @return The day corresponding to the adapter index. May be non-positive for position inputs
     * less than [MonthAdapter.firstPositionInMonth].
     */
    fun positionToDay(position: Int): Int {
        return position - firstPositionInMonth() + 1
    }

    /** Returns the adapter index representing the provided day.  */
    fun dayToPosition(day: Int): Int {
        val offsetFromFirst = day - 1
        return firstPositionInMonth() + offsetFromFirst
    }

    /** True when a provided adapter position is within the calendar month  */
    fun withinMonth(position: Int): Boolean {
        return position >= firstPositionInMonth() && position <= lastPositionInMonth()
    }

    /**
     * True when the provided adapter position is the smallest position for a value of [ ][MonthAdapter.getItemId].
     */
    fun isFirstInRow(position: Int): Boolean {
        return position % month.daysInWeek == 0
    }

    /**
     * True when the provided adapter position is the largest position for a value of [ ][MonthAdapter.getItemId].
     */
    fun isLastInRow(position: Int): Boolean {
        return (position + 1) % month.daysInWeek == 0
    }

    companion object {
        /**
         * The maximum number of weeks possible in any month. 6 for [java.util.GregorianCalendar].
         */
        val MAXIMUM_WEEKS = LocalDates.getLocalCalendar().getMaximum(Calendar.WEEK_OF_MONTH)
        private const val NO_DAY_NUMBER = -1
    }
}