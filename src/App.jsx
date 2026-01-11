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
    recurring: true
  })

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
    if (!formData.name || !formData.amount) return
    if (formData.recurring && !formData.day) return
    if (!formData.recurring && !formData.date) return

    const newBill = {
      id: Date.now(),
      name: formData.name,
      amount: parseFloat(formData.amount),
      recurring: formData.recurring,
      day: formData.recurring ? parseInt(formData.day) : null,
      date: !formData.recurring ? formData.date : null
    }

    setBills([...bills, newBill])
    resetForm()
  }

  const updateBill = () => {
    if (!formData.name || !formData.amount) return
    if (formData.recurring && !formData.day) return
    if (!formData.recurring && !formData.date) return

    setBills(bills.map(b =>
      b.id === editingId
        ? {
            ...b,
            name: formData.name,
            amount: parseFloat(formData.amount),
            recurring: formData.recurring,
            day: formData.recurring ? parseInt(formData.day) : null,
            date: !formData.recurring ? formData.date : null
          }
        : b
    ))
    resetForm()
  }

  const deleteBill = (id) => {
    setBills(bills.filter(b => b.id !== id))
    setSelectedDay(null)
  }

  const startEdit = (bill) => {
    setFormData({
      name: bill.name,
      amount: bill.amount.toString(),
      day: bill.recurring ? bill.day.toString() : '',
      date: !bill.recurring ? bill.date : '',
      recurring: bill.recurring
    })
    setEditingId(bill.id)
    setShowAddForm(true)
    setSelectedDay(null)
  }

  const resetForm = () => {
    setFormData({
      name: '',
      amount: '',
      day: '',
      date: '',
      recurring: true
    })
    setShowAddForm(false)
    setEditingId(null)
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
          onClick={() => hasBills && setSelectedDay(day)}
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
        <button onClick={() => changeMonth(-1)}>&lt;</button>
        <h2>
          {currentDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
        </h2>
        <button onClick={() => changeMonth(1)}>&gt;</button>
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

      <button className="add-button" onClick={() => setShowAddForm(true)}>
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
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              />
            </div>

            <div className="form-field">
              <label className="field-label">Amount</label>
              <input
                type="number"
                placeholder="0.00"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                step="0.01"
              />
            </div>

            {formData.recurring ? (
              <div className="form-field">
                <label className="field-label">Day of Month</label>
                <input
                  type="number"
                  placeholder="1-31"
                  value={formData.day}
                  onChange={(e) => setFormData({ ...formData, day: e.target.value })}
                  min="1"
                  max="31"
                />
              </div>
            ) : (
              <div className="form-field">
                <label className="field-label">Date</label>
                <input
                  type="date"
                  value={formData.date}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                />
              </div>
            )}

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
    </div>
  )
}

export default App
