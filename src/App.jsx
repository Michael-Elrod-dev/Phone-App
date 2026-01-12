import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [bills, setBills] = useState([])
  const [currentDate, setCurrentDate] = useState(new Date())
  const [showAddForm, setShowAddForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [selectedDay, setSelectedDay] = useState(null)
  const [formData, setFormData] = useState({
    name: '',
    amount: '',
    day: '',
    date: '',
    recurring: true,
    autopay: false
  })
  const [fieldErrors, setFieldErrors] = useState({})
  const [undoDelete, setUndoDelete] = useState(null)
  const [payDays, setPayDays] = useState([])
  const [showPayDaysForm, setShowPayDaysForm] = useState(false)
  const [payDayFormData, setPayDayFormData] = useState([])

  useEffect(() => {
    const saved = localStorage.getItem('bills')
    if (saved) {
      setBills(JSON.parse(saved))
    }
    const savedPayDays = localStorage.getItem('payDays')
    if (savedPayDays) {
      const parsed = JSON.parse(savedPayDays)

      // Migration: Check if old format (array of numbers)
      if (parsed.length > 0 && typeof parsed[0] === 'number') {
        // Convert old format to new format with default $0 amount
        const migrated = parsed.map(day => ({ day, amount: 0 }))
        setPayDays(migrated)
      } else {
        // Already new format
        setPayDays(parsed)
      }
    }
  }, [])

  useEffect(() => {
    localStorage.setItem('bills', JSON.stringify(bills))
  }, [bills])

  useEffect(() => {
    localStorage.setItem('payDays', JSON.stringify(payDays))
  }, [payDays])

  const addBill = () => {
    const errors = {}

    if (!formData.name) errors.name = true
    if (!formData.amount) errors.amount = true
    if (formData.recurring && !formData.day) errors.day = true
    if (!formData.recurring && !formData.date) errors.date = true

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      return
    }

    const newBill = {
      id: Date.now(),
      name: formData.name,
      amount: parseFloat(formData.amount),
      recurring: formData.recurring,
      day: formData.recurring ? parseInt(formData.day) : null,
      date: !formData.recurring ? formData.date : null,
      autopay: formData.autopay
    }

    setBills([...bills, newBill])
    resetForm()
  }

  const updateBill = () => {
    const errors = {}

    if (!formData.name) errors.name = true
    if (!formData.amount) errors.amount = true
    if (formData.recurring && !formData.day) errors.day = true
    if (!formData.recurring && !formData.date) errors.date = true

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      return
    }

    setBills(bills.map(b =>
      b.id === editingId
        ? {
            ...b,
            name: formData.name,
            amount: parseFloat(formData.amount),
            recurring: formData.recurring,
            day: formData.recurring ? parseInt(formData.day) : null,
            date: !formData.recurring ? formData.date : null,
            autopay: formData.autopay
          }
        : b
    ))
    resetForm()
  }

  const deleteBill = (id) => {
    const billToDelete = bills.find(b => b.id === id)
    setBills(bills.filter(b => b.id !== id))
    setSelectedDay(null)

    // Haptic feedback
    if (navigator.vibrate) {
      navigator.vibrate(50)
    }

    // Set undo state
    setUndoDelete({ bill: billToDelete, timeout: null })

    // Clear undo after 5 seconds
    const timeout = setTimeout(() => {
      setUndoDelete(null)
    }, 5000)

    setUndoDelete({ bill: billToDelete, timeout })
  }

  const undoDeleteBill = () => {
    if (undoDelete && undoDelete.bill) {
      setBills([...bills, undoDelete.bill])
      if (undoDelete.timeout) {
        clearTimeout(undoDelete.timeout)
      }
      setUndoDelete(null)

      // Haptic feedback
      if (navigator.vibrate) {
        navigator.vibrate(50)
      }
    }
  }

  const dismissUndo = () => {
    if (undoDelete && undoDelete.timeout) {
      clearTimeout(undoDelete.timeout)
    }
    setUndoDelete(null)
  }

  const startEdit = (bill) => {
    setFormData({
      name: bill.name,
      amount: bill.amount.toString(),
      day: bill.recurring ? bill.day.toString() : '',
      date: !bill.recurring ? bill.date : '',
      recurring: bill.recurring,
      autopay: bill.autopay || false
    })
    setEditingId(bill.id)
    setShowAddForm(true)
    setSelectedDay(null)

    // Haptic feedback
    if (navigator.vibrate) {
      navigator.vibrate(50)
    }
  }

  const resetForm = () => {
    setFormData({
      name: '',
      amount: '',
      day: '',
      date: '',
      recurring: true,
      autopay: false
    })
    setFieldErrors({})
    setShowAddForm(false)
    setEditingId(null)
  }

  const clearFieldError = (fieldName) => {
    if (fieldErrors[fieldName]) {
      setFieldErrors(prev => {
        const newErrors = { ...prev }
        delete newErrors[fieldName]
        return newErrors
      })
    }
  }

  const handleDayClick = (day, hasBills) => {
    if (hasBills) {
      setSelectedDay(day)

      // Haptic feedback
      if (navigator.vibrate) {
        navigator.vibrate(50)
      }
    }
  }

  const handleAddBillClick = () => {
    setShowAddForm(true)

    // Haptic feedback
    if (navigator.vibrate) {
      navigator.vibrate(50)
    }
  }

  const handleMonthChange = (delta) => {
    changeMonth(delta)

    // Haptic feedback
    if (navigator.vibrate) {
      navigator.vibrate(30)
    }
  }

  const getDaysInMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate()
  }

  const getFirstDayOfMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth(), 1).getDay()
  }

  const getBillsForDay = (day) => {
    const year = currentDate.getFullYear()
    const month = currentDate.getMonth()

    return bills.filter(bill => {
      if (bill.recurring) {
        return bill.day === day
      } else {
        const billDate = new Date(bill.date)
        return billDate.getFullYear() === year &&
               billDate.getMonth() === month &&
               billDate.getDate() === day
      }
    })
  }

  const getTotalForDay = (day) => {
    const dayBills = getBillsForDay(day)
    return dayBills.reduce((sum, bill) => sum + bill.amount, 0)
  }

  const changeMonth = (delta) => {
    const newDate = new Date(currentDate)
    newDate.setMonth(newDate.getMonth() + delta)
    setCurrentDate(newDate)
  }

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
      useGrouping: false
    }).format(amount)
  }

  const isToday = (day) => {
    const today = new Date()
    return today.getFullYear() === currentDate.getFullYear() &&
           today.getMonth() === currentDate.getMonth() &&
           today.getDate() === day
  }

  const isPayDay = (day) => {
    return payDays.some(pd => pd.day === day)
  }

  const getPayDayAmount = (day) => {
    const payDay = payDays.find(pd => pd.day === day)
    return payDay ? payDay.amount : 0
  }

  const getNextPayDays = (count) => {
    if (!payDays || payDays.length === 0) return []

    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const nextPayDayDates = []
    const sortedPayDays = [...payDays].sort((a, b) => a.day - b.day)

    let currentDate = new Date(today)

    while (nextPayDayDates.length < count) {
      const year = currentDate.getFullYear()
      const month = currentDate.getMonth()

      for (const pd of sortedPayDays) {
        // Skip if pay day doesn't exist in this month (e.g., day 31 in February)
        if (pd.day > getDaysInMonth(new Date(year, month, 1))) continue

        const payDayDate = new Date(year, month, pd.day)
        payDayDate.setHours(0, 0, 0, 0)

        // Only include if after today (not equal - pay day excluded)
        if (payDayDate > today) {
          nextPayDayDates.push(payDayDate)
          if (nextPayDayDates.length >= count) break
        }
      }

      // Move to next month if haven't found enough
      if (nextPayDayDates.length < count) {
        currentDate = new Date(year, month + 1, 1)
      }
    }

    return nextPayDayDates
  }

  const getBillsBeforeDate = (endDate) => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const normalizedEndDate = new Date(endDate)
    normalizedEndDate.setHours(0, 0, 0, 0)

    const billsInRange = []

    bills.forEach(bill => {
      if (bill.recurring) {
        // Check each month from today to endDate
        let checkDate = new Date(today)

        while (checkDate < normalizedEndDate) {
          const year = checkDate.getFullYear()
          const month = checkDate.getMonth()
          const daysInMonth = getDaysInMonth(new Date(year, month, 1))

          // Skip if bill day doesn't exist in this month
          if (bill.day > daysInMonth) {
            checkDate = new Date(year, month + 1, 1)
            continue
          }

          const billDate = new Date(year, month, bill.day)
          billDate.setHours(0, 0, 0, 0)

          // Include if billDate >= today AND billDate < endDate (excludes pay day)
          if (billDate >= today && billDate < normalizedEndDate) {
            billsInRange.push(bill)
          }

          checkDate = new Date(year, month + 1, 1)
        }
      } else {
        // One-time bill
        const billDate = new Date(bill.date)
        billDate.setHours(0, 0, 0, 0)

        if (billDate >= today && billDate < normalizedEndDate) {
          billsInRange.push(bill)
        }
      }
    })

    return billsInRange
  }

  const calculateTotalBeforeDate = (endDate) => {
    const billsInRange = getBillsBeforeDate(endDate)
    return billsInRange.reduce((sum, bill) => sum + bill.amount, 0)
  }

  const addPayDayRow = () => {
    setPayDayFormData([...payDayFormData, { day: '', amount: '' }])

    if (navigator.vibrate) {
      navigator.vibrate(50)
    }
  }

  const removePayDay = (index) => {
    setPayDayFormData(payDayFormData.filter((_, i) => i !== index))

    if (navigator.vibrate) {
      navigator.vibrate(50)
    }
  }

  const updatePayDayField = (index, field, value) => {
    const updated = [...payDayFormData]
    updated[index][field] = value
    setPayDayFormData(updated)
  }

  const handleEditPayDaysClick = () => {
    setPayDayFormData(payDays.map(pd => ({
      day: pd.day.toString(),
      amount: pd.amount.toString()
    })))
    setShowPayDaysForm(true)

    // Haptic feedback
    if (navigator.vibrate) {
      navigator.vibrate(50)
    }
  }

  const savePayDays = () => {
    // Filter out incomplete entries and parse values
    const validPayDays = payDayFormData
      .filter(pd => pd.day && pd.amount)
      .map(pd => ({
        day: parseInt(pd.day),
        amount: parseFloat(pd.amount)
      }))
      .filter(pd => !isNaN(pd.day) && !isNaN(pd.amount) && pd.day >= 1 && pd.day <= 31 && pd.amount >= 0)

    // Remove duplicates by day (keep last entry if duplicate)
    const uniquePayDays = validPayDays.reduce((acc, pd) => {
      const existingIndex = acc.findIndex(item => item.day === pd.day)
      if (existingIndex >= 0) {
        acc[existingIndex] = pd
      } else {
        acc.push(pd)
      }
      return acc
    }, [])

    // Sort by day
    uniquePayDays.sort((a, b) => a.day - b.day)

    setPayDays(uniquePayDays)
    setShowPayDaysForm(false)

    // Haptic feedback
    if (navigator.vibrate) {
      navigator.vibrate(50)
    }
  }

  const renderCalendar = () => {
    const daysInMonth = getDaysInMonth(currentDate)
    const firstDay = getFirstDayOfMonth(currentDate)
    const days = []

    for (let i = 0; i < firstDay; i++) {
      days.push(<div key={`empty-${i}`} className="calendar-day empty"></div>)
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const total = getTotalForDay(day)
      const hasBills = total > 0
      const today = isToday(day)
      const payday = isPayDay(day)

      days.push(
        <div
          key={day}
          className={`calendar-day ${today ? 'today' : ''} ${hasBills ? 'has-bills' : ''}`}
          onClick={() => handleDayClick(day, hasBills)}
        >
          <div className="day-number">{day}</div>
          {hasBills && (
            <div className="day-total">{formatCurrency(total)}</div>
          )}
          {payday && (
            <div className="payday-amount">{formatCurrency(getPayDayAmount(day))}</div>
          )}
        </div>
      )
    }

    return days
  }

  const selectedDayBills = selectedDay ? getBillsForDay(selectedDay) : []

  return (
    <div className="app">
      <div className="month-selector">
        <button onClick={() => handleMonthChange(-1)}>&lt;</button>
        <h2>
          {currentDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
        </h2>
        <button onClick={() => handleMonthChange(1)}>&gt;</button>
      </div>

      <div className="calendar">
        <div className="calendar-header">
          <div className="calendar-day-name">Sun</div>
          <div className="calendar-day-name">Mon</div>
          <div className="calendar-day-name">Tue</div>
          <div className="calendar-day-name">Wed</div>
          <div className="calendar-day-name">Thu</div>
          <div className="calendar-day-name">Fri</div>
          <div className="calendar-day-name">Sat</div>
        </div>
        <div className="calendar-grid">
          {renderCalendar()}
        </div>
      </div>

      {/* Bills Before Pay Days Summary */}
      <div className="payday-summary">
        {payDays.length === 0 ? (
          <div className="payday-summary-message">
            Configure pay days to see upcoming bills summary
          </div>
        ) : (
          <div className="payday-summary-content">
            {(() => {
              const nextPayDays = getNextPayDays(2)
              return (
                <>
                  {nextPayDays.length > 0 && (
                    <div className="payday-summary-row">
                      <span className="payday-summary-label">Before Next Pay:</span>
                      <span className="payday-summary-amount">
                        {formatCurrency(calculateTotalBeforeDate(nextPayDays[0]))}
                      </span>
                    </div>
                  )}
                  {nextPayDays.length === 2 && (
                    <div className="payday-summary-row">
                      <span className="payday-summary-label">Before 2nd Pay:</span>
                      <span className="payday-summary-amount">
                        {formatCurrency(calculateTotalBeforeDate(nextPayDays[1]))}
                      </span>
                    </div>
                  )}
                </>
              )
            })()}
          </div>
        )}
      </div>

      <button className="add-button" onClick={handleAddBillClick}>
        + Add Bill
      </button>

      <button className="edit-paydays-button" onClick={handleEditPayDaysClick}>
        Edit Pay Days
      </button>

      {showAddForm && (
        <div className="modal-overlay" onClick={resetForm}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>{editingId ? 'Edit Bill' : 'Add Bill'}</h3>

            <div className="recurring-toggle">
              <label>
                <input
                  type="radio"
                  name="recurring"
                  checked={formData.recurring}
                  onChange={() => setFormData({ ...formData, recurring: true, date: '' })}
                />
                Recurring (every month)
              </label>
              <label>
                <input
                  type="radio"
                  name="recurring"
                  checked={!formData.recurring}
                  onChange={() => setFormData({ ...formData, recurring: false, day: '' })}
                />
                One-time
              </label>
            </div>

            <div className="form-field">
              <label className="field-label">Bill Name</label>
              <input
                type="text"
                placeholder="e.g., Rent, Electric, Internet"
                value={formData.name}
                onChange={(e) => {
                  setFormData({ ...formData, name: e.target.value })
                  clearFieldError('name')
                }}
                className={fieldErrors.name ? 'error' : ''}
              />
            </div>

            <div className="form-field">
              <label className="field-label">Amount</label>
              <input
                type="number"
                placeholder="0.00"
                value={formData.amount}
                onChange={(e) => {
                  setFormData({ ...formData, amount: e.target.value })
                  clearFieldError('amount')
                }}
                step="0.01"
                className={fieldErrors.amount ? 'error' : ''}
              />
            </div>

            {formData.recurring ? (
              <div className="form-field">
                <label className="field-label">Day of Month</label>
                <input
                  type="number"
                  placeholder="1-31"
                  value={formData.day}
                  onChange={(e) => {
                    setFormData({ ...formData, day: e.target.value })
                    clearFieldError('day')
                  }}
                  min="1"
                  max="31"
                  className={fieldErrors.day ? 'error' : ''}
                />
              </div>
            ) : (
              <div className="form-field">
                <label className="field-label">Date</label>
                <input
                  type="date"
                  value={formData.date}
                  onChange={(e) => {
                    setFormData({ ...formData, date: e.target.value })
                    clearFieldError('date')
                  }}
                  className={fieldErrors.date ? 'error' : ''}
                />
              </div>
            )}

            <div className="form-field">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  checked={formData.autopay}
                  onChange={(e) => setFormData({ ...formData, autopay: e.target.checked })}
                />
                Autopay Enabled
              </label>
            </div>

            <div className="form-buttons">
              <button onClick={editingId ? updateBill : addBill}>
                {editingId ? 'Update' : 'Add'}
              </button>
              <button onClick={resetForm}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {selectedDay && (
        <div className="modal-overlay" onClick={() => setSelectedDay(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Bills Due - {currentDate.toLocaleDateString('en-US', { month: 'long' })} {selectedDay}</h3>

            <div className="bills-list">
              {selectedDayBills.map(bill => (
                <div key={bill.id} className="bill-item">
                  <div className="bill-info">
                    <div className="bill-name">{bill.name}</div>
                    {bill.recurring && (
                      <div className="bill-recurring">Recurring</div>
                    )}
                    {bill.autopay && (
                      <div className="bill-autopay">Autopay Enabled</div>
                    )}
                  </div>
                  <div className="bill-amount">{formatCurrency(bill.amount)}</div>
                  <div className="bill-actions">
                    <button onClick={() => startEdit(bill)}>Edit</button>
                    <button onClick={() => deleteBill(bill.id)}>Delete</button>
                  </div>
                </div>
              ))}
            </div>

            <div className="modal-total">
              Total: {formatCurrency(getTotalForDay(selectedDay))}
            </div>

            <button className="close-button" onClick={() => setSelectedDay(null)}>
              Close
            </button>
          </div>
        </div>
      )}

      {undoDelete && (
        <div className="toast">
          <span className="toast-message">Bill deleted</span>
          <button className="toast-button" onClick={undoDeleteBill}>Undo</button>
          <button className="toast-dismiss" onClick={dismissUndo}>×</button>
        </div>
      )}

      {showPayDaysForm && (
        <div className="modal-overlay" onClick={() => setShowPayDaysForm(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Edit Pay Days</h3>

            <div className="payday-list">
              {payDayFormData.map((pd, index) => (
                <div key={index} className="payday-entry">
                  <div className="payday-fields">
                    <div className="form-field payday-day-field">
                      <label className="field-label">Day</label>
                      <input
                        type="number"
                        placeholder="1-31"
                        value={pd.day}
                        onChange={(e) => updatePayDayField(index, 'day', e.target.value)}
                        min="1"
                        max="31"
                      />
                    </div>
                    <div className="form-field payday-amount-field">
                      <label className="field-label">Amount</label>
                      <input
                        type="number"
                        placeholder="0.00"
                        value={pd.amount}
                        onChange={(e) => updatePayDayField(index, 'amount', e.target.value)}
                        step="0.01"
                      />
                    </div>
                  </div>
                  <button className="remove-payday-button" onClick={() => removePayDay(index)}>
                    ×
                  </button>
                </div>
              ))}
            </div>

            <button className="add-payday-button" onClick={addPayDayRow}>
              + Add Pay Day
            </button>

            <div className="form-buttons">
              <button onClick={savePayDays}>Save</button>
              <button onClick={() => setShowPayDaysForm(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
