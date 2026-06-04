package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MaterialInventoryRepository extends
        JpaRepository<MaterialInventoryEntity, Long>,
        JpaSpecificationExecutor<MaterialInventoryEntity> {

    Optional<MaterialInventoryEntity> findByMaterialIdAndWarehouseId(
            Long materialId,
            Long warehouseId
    );

    List<MaterialInventoryEntity> findByMaterialId(Long materialId);

    @Query("SELECT new com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO(" +
            "m.id, c.id, c.name, m.code, m.name, m.unit, m.thumbnail, m.thumbnailPublicId, m.status, " +

            // 1. Count số kho chứa vật liệu này (chỉ đếm kho có tồn thực tế hoặc có cấu hình kho)
            "COUNT(DISTINCT i.warehouse.id) AS long, " +

            // 2. Tính tổng số lượng OnHand (Sử dụng COALESCE để đổi NULL thành 0 nếu chưa từng nhập kho)
            "SUM(CAST(COALESCE(i.onHand, 0) AS long)) AS onHand, " +

            // 3. Tính tổng số lượng Reserved
            "SUM(CAST(COALESCE(i.reserved, 0) AS long)) AS reserved, " +

            // 4. Tính tổng số lượng Available có thể bán
            "SUM(CAST((COALESCE(i.onHand, 0) - COALESCE(i.reserved, 0)) AS long)) AS available, " +

            // 5. Ngưỡng cảnh báo (Lấy ngưỡng cao nhất của các kho làm mốc đại diện bên ngoài)
            "CAST(COALESCE(m.lowStockThreshold, 0) AS long), " +

            // 6. hasLowStockWarehouse: Báo true nếu có ít nhất 1 kho có onHand <= ngưỡng (và onHand > 0)
            "CASE WHEN SUM(CASE WHEN i.onHand <= m.lowStockThreshold AND i.onHand > 0 THEN 1 ELSE 0 END) > 0 THEN true ELSE false END, " +

            // 7. hasOutOfStockWarehouse: Báo true nếu có ít nhất 1 kho có hàng bị cháy hoàn toàn (onHand = 0)
            "CASE WHEN SUM(CASE WHEN i.onHand = 0 THEN 1 ELSE 0 END) > 0 THEN true ELSE false END, " +

            // 8. Giá vốn bình quân gia quyền hệ thống = Tổng tiền hàng 3 kho / Tổng số lượng 3 kho
            "CASE WHEN SUM(COALESCE(i.onHand, 0)) > 0 " +
            "THEN (SUM(CAST(COALESCE(i.onHand, 0) AS double) * CAST(COALESCE(i.costPrice, 0) AS double))) / SUM(CAST(COALESCE(i.onHand, 0) AS double)) ELSE 0 END AS costPrice" +
            ") " +
            "FROM MaterialEntity m " +
            "JOIN m.category c " +
            "LEFT JOIN MaterialInventoryEntity i ON i.material.id = m.id " +
            "WHERE (:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND ((:status IS NULL AND m.status <> 'DELETED') OR (:status IS NOT NULL AND m.status = :status)) " +
            "AND (:warehouseId IS NULL OR i.warehouse.id = :warehouseId) " +
            "GROUP BY m.id, c.id, c.name, m.code, m.name, m.unit, m.thumbnail, m.thumbnailPublicId, m.status " +
            "HAVING (:lowStock IS NULL OR :lowStock = false OR SUM(CASE WHEN i.onHand <= m.lowStockThreshold AND i.onHand > 0 THEN 1 ELSE 0 END) > 0) " +
            "AND (:outOfStock IS NULL OR :outOfStock = false OR SUM(CASE WHEN i.onHand = 0 THEN 1 ELSE 0 END) > 0)")
    Page<MaterialInventorySummaryResponseDTO> getInventorySummary(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") Status status,
            @Param("warehouseId") Long warehouseId,
            @Param("lowStock") Boolean lowStock,
            @Param("outOfStock") Boolean outOfStock,
            Pageable pageable);

    @Query("""
        select count(distinct i.material.id)
        from MaterialInventoryEntity i
        where i.warehouse.id = :warehouseId
          and i.material.status <> 'DELETED'
          and (i.onHand > 0 or i.reserved > 0)
    """)
    Long countMaterialsByWarehouseId(Long warehouseId);

    @Query("""
        select
            i.warehouse.id as warehouseId,
            count(distinct i.material.id) as materialCount
        from MaterialInventoryEntity i
        where i.warehouse.id in :warehouseIds
          and i.material.status <> 'DELETED'
          and (i.onHand > 0 or i.reserved > 0)
        group by i.warehouse.id
    """)
    List<Object[]> countMaterialsByWarehouseIds(Collection<Long> warehouseIds);
}
