import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import {
  BudgetEntry,
  BudgetSummary,
  Employee,
  EmployeeForm,
  EmployeeSalaryRecord,
  EmployeeSalaryRecordForm,
  ViewName
} from './models';

const API_BASE_URL = environment.apiBaseUrl;

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css',
  encapsulation: ViewEncapsulation.None
})
export class App implements OnInit {
  entries: BudgetEntry[] = [];
  summary: BudgetSummary = { totalIncome: 0, totalExpense: 0, balance: 0 };
  selectedView: ViewName = 'dashboard';
  selectedEntry: BudgetEntry | null = null;
  isEditing = false;
  editEntryId: number | null = null;
  employees: Employee[] = [];
  selectedEmployeeId: number | null = null;
  employeeForm: EmployeeForm = this.createEmployeeForm();
  employeeEditingId: number | null = null;
  employeeReadOnly = false;
  salaryRecords: EmployeeSalaryRecord[] = [];
  salaryRecordForm: EmployeeSalaryRecordForm = this.createSalaryRecordForm();
  salaryRecordEditingId: number | null = null;
  salaryRecordReadOnly = false;
  employeeError = '';
  salaryRecordError = '';

  get isReadOnly(): boolean {
    return this.selectedEntry !== null && !this.isEditing;
  }

  form: BudgetEntry = {
    title: '',
    amount: 0,
    type: 'INCOME',
    category: '',
    description: '',
    entryDate: new Date().toISOString().slice(0, 10)
  };

  constructor(private readonly http: HttpClient) {}

  get pageTitle(): string {
    if (this.selectedView === 'employees') {
      return this.employeeReadOnly ? 'View Employee' : 'Employees';
    }
    if (this.selectedView === 'salary-records') {
      return 'Advances & Deductions';
    }
    if (this.selectedView === 'income') {
      return 'Income';
    }
    if (this.selectedView === 'expense') {
      return 'Expenses';
    }
    return this.selectedView === 'reports' ? 'Reports' : 'My Monthly Budget';
  }

  get showsBudgetSummary(): boolean {
    return this.selectedView !== 'employees' && this.selectedView !== 'salary-records';
  }

  ngOnInit(): void {
    this.loadSummary();
    this.loadEntries();
  }

  get incomeEntries(): BudgetEntry[] {
    return this.entries.filter((entry) => entry.type === 'INCOME');
  }

  get expenseEntries(): BudgetEntry[] {
    return this.entries.filter((entry) => entry.type === 'EXPENSE');
  }

  selectView(view: ViewName): void {
    this.selectedView = view;
    if (view === 'employees') {
      this.startNewEmployee();
      this.loadEmployees();
      return;
    }
    if (view === 'salary-records') {
      this.salaryRecordEditingId = null;
      this.salaryRecordReadOnly = false;
      this.salaryRecordForm = this.createSalaryRecordForm();
      this.loadEmployees();
      if (this.selectedEmployeeId !== null) {
        this.loadSalaryRecords();
      }
      return;
    }
    this.resetForm(view === 'expense' ? 'EXPENSE' : 'INCOME');
  }

  private createEmployeeForm(): EmployeeForm {
    return {
      employeeCode: '',
      fullName: '',
      department: '',
      phone: '',
      monthlySalary: 0,
      active: true
    };
  }

  private createSalaryRecordForm(): EmployeeSalaryRecordForm {
    return {
      type: 'ADVANCE',
      amount: 0,
      recordDate: new Date().toISOString().slice(0, 10),
      description: ''
    };
  }

  loadEmployees(): void {
    this.http.get<Employee[]>(`${API_BASE_URL}/api/employees`).subscribe({
      next: (data) => {
        this.employees = data;
        if (this.selectedEmployeeId !== null && !data.some((employee) => employee.id === this.selectedEmployeeId)) {
          this.selectedEmployeeId = null;
          this.salaryRecords = [];
        }
      },
      error: () => {
        this.employeeError = 'Unable to load employees. Please try again.';
      }
    });
  }

  startNewEmployee(): void {
    this.employeeEditingId = null;
    this.employeeReadOnly = false;
    this.employeeForm = this.createEmployeeForm();
    this.employeeError = '';
  }

  editEmployee(employee: Employee): void {
    this.employeeEditingId = employee.id;
    this.employeeReadOnly = false;
    this.employeeForm = {
      employeeCode: employee.employeeCode,
      fullName: employee.fullName,
      department: employee.department ?? '',
      phone: employee.phone ?? '',
      monthlySalary: Number(employee.monthlySalary),
      active: employee.active
    };
    this.employeeError = '';
  }

  saveEmployee(): void {
    if (!this.employeeForm.employeeCode.trim() || !this.employeeForm.fullName.trim() || this.employeeForm.monthlySalary <= 0) {
      return;
    }

    const request = this.employeeEditingId === null
      ? this.http.post<Employee>(`${API_BASE_URL}/api/employees`, this.employeeForm)
      : this.http.put<Employee>(`${API_BASE_URL}/api/employees/${this.employeeEditingId}`, this.employeeForm);

    request.subscribe({
      next: () => {
        this.employeeError = '';
        this.employeeEditingId = null;
        this.employeeForm = this.createEmployeeForm();
        this.employeeReadOnly = false;
        this.selectedEmployeeId = null;
        this.loadEmployees();
      },
      error: (error) => {
        this.employeeError = error.status === 409
          ? 'That employee code is already in use.'
          : 'Unable to save this employee. Please check the details and try again.';
      }
    });
  }

  toggleEmployeeStatus(employee: Employee): void {
    const request = {
      employeeCode: employee.employeeCode,
      fullName: employee.fullName,
      department: employee.department,
      phone: employee.phone,
      monthlySalary: employee.monthlySalary,
      active: !employee.active
    };

    this.http.put<Employee>(`${API_BASE_URL}/api/employees/${employee.id}`, request).subscribe({
      next: (updated) => {
        this.employees = this.employees.map((item) => item.id === updated.id ? updated : item);
      },
      error: () => {
        this.employeeError = 'Unable to update employee status. Please try again.';
      }
    });
  }

  selectEmployee(employee: Employee): void {
    this.selectedEmployeeId = employee.id;
    this.selectedView = 'salary-records';
    this.salaryRecordEditingId = null;
    this.salaryRecordReadOnly = false;
    this.salaryRecordForm = this.createSalaryRecordForm();
    this.salaryRecordError = '';
    this.loadSalaryRecords();
  }

  viewEmployee(employee: Employee): void {
    this.selectedEmployeeId = employee.id;
    this.selectedView = 'employees';
    this.employeeEditingId = employee.id;
    this.employeeReadOnly = true;
    this.employeeForm = {
      employeeCode: employee.employeeCode,
      fullName: employee.fullName,
      department: employee.department ?? '',
      phone: employee.phone ?? '',
      monthlySalary: Number(employee.monthlySalary),
      active: employee.active
    };
    this.employeeError = '';
  }

  backToEmployeeList(): void {
    this.selectedView = 'employees';
    this.cancelSalaryRecordEdit();
  }

  get selectedEmployee(): Employee | undefined {
    return this.employees.find((employee) => employee.id === this.selectedEmployeeId);
  }

  loadSalaryRecords(): void {
    if (this.selectedEmployeeId === null) {
      this.salaryRecords = [];
      return;
    }

    this.http.get<EmployeeSalaryRecord[]>(
      `${API_BASE_URL}/api/employees/${this.selectedEmployeeId}/salary-records`
    ).subscribe({
      next: (data) => {
        this.salaryRecords = data;
      },
      error: () => {
        this.salaryRecordError = 'Unable to load employee advances and deductions.';
      }
    });
  }

  saveSalaryRecord(): void {
    if (this.selectedEmployeeId === null || this.salaryRecordForm.amount <= 0) {
      return;
    }

    const url = `${API_BASE_URL}/api/employees/${this.selectedEmployeeId}/salary-records`;
    const request = this.salaryRecordEditingId === null
      ? this.http.post<EmployeeSalaryRecord>(url, this.salaryRecordForm)
      : this.http.put<EmployeeSalaryRecord>(`${url}/${this.salaryRecordEditingId}`, this.salaryRecordForm);

    request.subscribe({
      next: () => {
        this.salaryRecordEditingId = null;
        this.salaryRecordReadOnly = false;
        this.salaryRecordForm = this.createSalaryRecordForm();
        this.salaryRecordError = '';
        this.loadSalaryRecords();
      },
      error: () => {
        this.salaryRecordError = 'Unable to save this advance or deduction. Please try again.';
      }
    });
  }

  editSalaryRecord(record: EmployeeSalaryRecord): void {
    this.salaryRecordEditingId = record.id;
    this.salaryRecordReadOnly = false;
    this.salaryRecordForm = {
      type: record.type,
      amount: Number(record.amount),
      recordDate: record.recordDate,
      description: record.description ?? ''
    };
    this.salaryRecordError = '';
  }

  viewSalaryRecord(record: EmployeeSalaryRecord): void {
    this.salaryRecordEditingId = record.id;
    this.salaryRecordReadOnly = true;
    this.salaryRecordForm = {
      type: record.type,
      amount: Number(record.amount),
      recordDate: record.recordDate,
      description: record.description ?? ''
    };
    this.salaryRecordError = '';
  }

  closeSalaryRecordView(): void {
    this.salaryRecordEditingId = null;
    this.salaryRecordReadOnly = false;
    this.salaryRecordForm = this.createSalaryRecordForm();
  }

  cancelSalaryRecordEdit(): void {
    this.salaryRecordEditingId = null;
    this.salaryRecordReadOnly = false;
    this.salaryRecordForm = this.createSalaryRecordForm();
    this.salaryRecordError = '';
  }

  deleteSalaryRecord(record: EmployeeSalaryRecord): void {
    if (this.selectedEmployeeId === null || !window.confirm('Delete this salary record?')) {
      return;
    }

    this.http.delete(
      `${API_BASE_URL}/api/employees/${this.selectedEmployeeId}/salary-records/${record.id}`
    ).subscribe({
      next: () => {
        if (this.salaryRecordEditingId === record.id) {
          this.closeSalaryRecordView();
        }
        this.loadSalaryRecords();
      },
      error: () => {
        this.salaryRecordError = 'Unable to delete this salary record. Please try again.';
      }
    });
  }

  resetForm(type: 'INCOME' | 'EXPENSE' = 'INCOME'): void {
    this.form = {
      title: '',
      amount: 0,
      type,
      category: '',
      description: '',
      entryDate: new Date().toISOString().slice(0, 10)
    };
    this.isEditing = false;
    this.editEntryId = null;
    this.selectedEntry = null;
  }

  viewEntry(entry: BudgetEntry): void {
    this.isEditing = false;
    this.editEntryId = null;
    this.selectedEntry = entry;
    this.form = {
      ...entry,
      amount: Number(entry.amount),
      entryDate: entry.entryDate
    };
  }

  editEntry(entry: BudgetEntry): void {
    this.selectedEntry = entry;
    this.isEditing = true;
    this.editEntryId = entry.id ?? null;
    this.form = {
      ...entry,
      amount: Number(entry.amount),
      entryDate: entry.entryDate
    };
  }

  cancelEdit(): void {
    this.resetForm(this.selectedView === 'expense' ? 'EXPENSE' : 'INCOME');
  }

  loadEntries(): void {
    this.http.get<BudgetEntry[]>(`${API_BASE_URL}/api/budget`).subscribe({
      next: (data) => {
        this.entries = data;
        this.loadSummary();
      },
      error: () => {
        this.entries = [];
      }
    });
  }

  loadSummary(): void {
    this.http.get<BudgetSummary>(`${API_BASE_URL}/api/budget/summary`).subscribe({
      next: (data) => {
        this.summary = data;
      },
      error: () => {
        this.summary = { totalIncome: 0, totalExpense: 0, balance: 0 };
      }
    });
  }

  submitForm(): void {
    if (!this.form.title || !this.form.category || this.form.amount <= 0) {
      return;
    }

    const payload = {
      ...this.form,
      amount: Number(this.form.amount)
    };

    const request = this.isEditing && this.editEntryId !== null
      ? this.http.put<BudgetEntry>(`${API_BASE_URL}/api/budget/${this.editEntryId}`, payload)
      : this.http.post<BudgetEntry>(`${API_BASE_URL}/api/budget`, payload);

    request.subscribe({
      next: () => {
        this.resetForm(this.selectedView === 'expense' ? 'EXPENSE' : 'INCOME');
        this.loadEntries();
      },
      error: () => {
        alert('Unable to save this budget entry right now.');
      }
    });
  }

  deleteEntry(id?: number): void {
    if (!id) {
      return;
    }

    this.http.delete(`${API_BASE_URL}/api/budget/${id}`).subscribe({
      next: () => {
        this.loadEntries();
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }

}
