import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

const API_BASE_URL = environment.apiBaseUrl;

type ViewName = 'dashboard' | 'income' | 'expense' | 'reports';

interface BudgetEntry {
  id?: number;
  title: string;
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  category: string;
  description: string;
  entryDate: string;
}

interface BudgetSummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
}

interface CategoryBreakdown {
  category: string;
  total: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App implements OnInit {
  @ViewChild('titleInput') titleInput?: ElementRef<HTMLInputElement>;

  entries: BudgetEntry[] = [];
  summary: BudgetSummary = { totalIncome: 0, totalExpense: 0, balance: 0 };
  selectedView: ViewName = 'dashboard';
  selectedEntry: BudgetEntry | null = null;
  isEditing = false;
  editEntryId: number | null = null;

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

  get categoryBreakdown(): CategoryBreakdown[] {
    const totals = new Map<string, number>();

    this.entries.forEach((entry) => {
      const current = totals.get(entry.category) ?? 0;
      totals.set(entry.category, current + Number(entry.amount));
    });

    return Array.from(totals.entries())
      .map(([category, total]) => ({ category, total }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 6);
  }

  get monthlyTrend(): Array<{ label: string; income: number; expense: number }> {
    const months = Array.from({ length: 6 }, (_, index) => {
      const date = new Date();
      date.setMonth(date.getMonth() - (5 - index));
      return date;
    });

    return months.map((date) => {
      const label = date.toLocaleString('en-US', { month: 'short' });
      const income = this.entries
        .filter((entry) => entry.type === 'INCOME' && new Date(entry.entryDate).getMonth() === date.getMonth() && new Date(entry.entryDate).getFullYear() === date.getFullYear())
        .reduce((sum, entry) => sum + Number(entry.amount), 0);
      const expense = this.entries
        .filter((entry) => entry.type === 'EXPENSE' && new Date(entry.entryDate).getMonth() === date.getMonth() && new Date(entry.entryDate).getFullYear() === date.getFullYear())
        .reduce((sum, entry) => sum + Number(entry.amount), 0);

      return { label, income, expense };
    });
  }

  selectView(view: ViewName): void {
    this.selectedView = view;
    this.resetForm(view === 'expense' ? 'EXPENSE' : 'INCOME');
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
    setTimeout(() => this.titleInput?.nativeElement.focus());
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
    setTimeout(() => this.titleInput?.nativeElement.focus());
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

  formatType(type: 'INCOME' | 'EXPENSE'): string {
    return type === 'INCOME' ? 'Income' : 'Expense';
  }

  percentageOfTotal(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return (value / total) * 100;
  }
}
