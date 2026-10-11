export type ViewName = 'dashboard' | 'income' | 'expense' | 'reports' | 'employees' | 'salary-records';
export type SalaryRecordType = 'ADVANCE' | 'DEDUCTION';

export interface BudgetEntry {
  id?: number;
  title: string;
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  category: string;
  description: string;
  entryDate: string;
}

export interface BudgetSummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
}

export interface CategoryBreakdown {
  category: string;
  total: number;
}

export interface MonthlyTrend {
  label: string;
  income: number;
  expense: number;
}

export interface Employee {
  id: number;
  employeeCode: string;
  fullName: string;
  department: string | null;
  phone: string | null;
  monthlySalary: number;
  active: boolean;
}

export interface EmployeeForm {
  employeeCode: string;
  fullName: string;
  department: string;
  phone: string;
  monthlySalary: number;
  active: boolean;
}

export interface EmployeeSalaryRecord {
  id: number;
  employeeId: number;
  type: SalaryRecordType;
  amount: number;
  recordDate: string;
  description: string | null;
}

export interface EmployeeSalaryRecordForm {
  type: SalaryRecordType;
  amount: number;
  recordDate: string;
  description: string;
}
