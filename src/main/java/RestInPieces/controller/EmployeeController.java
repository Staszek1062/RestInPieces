package RestInPieces.controller;

import RestInPieces.model.Employee;
import RestInPieces.model.EmployeeDto;
import RestInPieces.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    public static final Long EMPTY_ID = null;

    private final EmployeeService employeeService;
    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> getEmployees(){
        List<EntityModel<Employee>> employees =employeeService.getEmployees().stream()
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withRel("employees")
                )).toList();
        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).getEmployees()).withSelfRel()
        );
    }
    @GetMapping("/employee/{id}")
    public EntityModel<Employee> getEmployee(@PathVariable Long id){
        return EntityModel.of(employeeService.getEmployee(id),
                linkTo(methodOn(EmployeeController.class).getEmployee(id)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("employees"),
                linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("deactivate")
                        .withRel("deactivate")
        );
    }
    @PostMapping("/employee")
    public ResponseEntity<Object> createEmployees(@RequestBody EmployeeDto employeeDto){
        Employee employee = employeeService.createEmployee(new Employee(
                EMPTY_ID,
                employeeDto.getFirstname(),
                employeeDto.getLastname(),
                employeeDto.isActive(),
                employeeDto.getCreated()
        ));
        UriComponents uriComponents = UriComponentsBuilder.
                fromHttpUrl("http://localhost:8080/employee/{id}").
                buildAndExpand(employee.getId());

        return ResponseEntity.created(uriComponents.toUri())
                .body(employee);
    }
    @PutMapping("/employee/{id}")
    public ResponseEntity<Object> updateEmployees(@PathVariable Long id, @RequestBody EmployeeDto employeeDto){
        employeeService.updateEmployee(new Employee(
                id,
                employeeDto.getFirstname(),
                employeeDto.getLastname(),
                employeeDto.isActive(),
                employeeDto.getCreated()
        ));
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/employee/{id}")
    @RequestMapping(value ="/employee/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteEmployees(@PathVariable Long id){
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/employee/{id}/deactivate")
    public void deactivateEmployee(@PathVariable Long id){

    }
}
