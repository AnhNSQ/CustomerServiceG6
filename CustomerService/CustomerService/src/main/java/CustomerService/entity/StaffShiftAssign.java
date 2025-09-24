package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "staff_shift_assign")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffShiftAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_shift_id")
    private Long staffShiftId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Constructor để tạo staff shift assignment mới
    public StaffShiftAssign(Staff staff, Shift shift, LocalDate date) {
        this.staff = staff;
        this.shift = shift;
        this.date = date;
    }
}
