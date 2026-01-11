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
    notes: ''
  })
  const [fieldErrors, setFieldErrors] = useState({})
  const [undoDelete, setUndoDelete] = useState(null)

  useEffect(() => {
    const saved = localStorage.getItem('bills')
    if (saved) {
      setBills(JSON.parse(saved))
    }
  }, [])

  useEffect(() => {
    localStorage.setItem('bills', JSON.stringify(bills))
  }, [bills])

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
      notes: formData.notes || ''
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
            notes: formData.notes || ''
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
      notes: bill.notes || ''
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
      notes: ''
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
      currency: 'USD'
    }).format(amount)
  }

  const isToday = (day) => {
    const today = new Date()
    return today.getFullYear() === currentDate.getFullYear() &&
           today.getMonth() === currentDate.getMonth() &&
           today.getDate() === day
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

      <button className="add-button" onClick={handleAddBillClick}>
        + Add Bill
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
              <label className="field-label">Notes (optional)</label>
              <textarea
                placeholder="e.g., Autopay enabled, Due date flexible"
                value={formData.notes}
                onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                rows="3"
              />
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
                    {bill.notes && (
                      <div className="bill-notes">{bill.notes}</div>
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
    </div>
  )
}

export default App
