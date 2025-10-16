import com.example.pizzumburgum.component.creacion.Producto;
import com.example.pizzumburgum.enums.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByTipo(TipoProducto tipo);

    List<Producto> findByDisponibleTrue();

    List<Producto> findByActivoTrue();

    List<Producto> findByTipoAndDisponibleTrue(TipoProducto tipo);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.disponible = true")
    List<Producto> findByCategoriaIdAndDisponibleTrue(@Param("categoriaId") Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.disponible = true")
    List<Producto> findByStockDisponible();

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    Optional<Producto> findByNombre(String nombre);
}