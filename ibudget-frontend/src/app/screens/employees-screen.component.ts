import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { Employee, EmployeeForm } from '../models';

@Component({
  selector: 'app-employees-screen',
  templateUrl: './employees-screen.component.html',
  standalone: false
})
export class EmployeesScreenComponent implements OnChanges {
  @ViewChild('firstEditable') firstEditable?: ElementRef<HTMLInputElement>;
  pageIndex = 0;
  pageSize = 10;

  @Input() employees: Employee[] = [];
  @Input() form!: EmployeeForm;
  @Input() editingId: number | null = null;
  @Input() readOnly = false;
  @Input() selectedEmployeeId: number | null = null;
  @Input() error = '';
  @Output() add = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();
  @Output() view = new EventEmitter<Employee>();
  @Output() edit = new EventEmitter<Employee>();
  @Output() toggleStatus = new EventEmitter<Employee>();

  ngOnChanges(changes: SimpleChanges): void {
    this.keepPageInRange();
    if (changes['readOnly'] || changes['editingId'] || changes['form']) {
      this.focusFirstEditable();
    }
  }

  get visibleEmployees(): Employee[] {
    const start = this.pageIndex * this.pageSize;
    return this.employees.slice(start, start + this.pageSize);
  }

  changePage(index: number): void {
    this.pageIndex = index;
  }

  changePageSize(size: number): void {
    this.pageSize = size;
    this.pageIndex = 0;
  }

  get title(): string {
    return this.readOnly ? 'View employee' : this.editingId === null ? 'Add employee' : 'Edit employee';
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }

  private focusFirstEditable(): void {
    if (this.readOnly) {
      return;
    }
    setTimeout(() => this.firstEditable?.nativeElement.focus());
  }

  private keepPageInRange(): void {
    const lastPage = Math.max(0, Math.ceil(this.employees.length / this.pageSize) - 1);
    this.pageIndex = Math.min(this.pageIndex, lastPage);
  }
}
