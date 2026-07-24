<tr class:table-active={category.selected}>
  <!-- Actions Column -->
  <th scope="row" class="align-middle text-center" style="width: 60px;">
    <div class="dropdown position-static">
      <button
        type="button"
        class="btn btn-link px-2"
        data-bs-toggle="dropdown"
        title={$_('actions')}
        aria-label={$_('actions')}>
        <span class="fas fa-ellipsis-v"></span>
      </button>
      <div class="dropdown-menu animate__animated animate__fadeIn">
        <button type="button" class="dropdown-item" on:click={() => onEdit(category)}>
          <i class="fas fa-pen me-2"></i>
          {$_('faq.edit')}
        </button>
        <button type="button" class="dropdown-item text-danger" on:click={() => onDelete(category)}>
          <i class="fas fa-trash me-2"></i>
          {$_('faq.delete')}
        </button>
      </div>
    </div>
  </th>

  <!-- ID Column -->
  <td class="align-middle text-center" style="width: 60px;">
    <code>{category.id}</code>
  </td>

  <!-- Name -->
  <td class="align-middle" style="max-width: 300px;">
    <button
      type="button"
      class="btn btn-link p-0 focus-ring rounded text-decoration-none d-block text-truncate fw-bold w-100 text-start"
      on:click={() => onEdit(category)}
      title={$_('faq.edit')}>
      {category.name}
    </button>
  </td>

  <!-- Display Order -->
  <td class="align-middle text-center">
    {category.displayOrder}
  </td>
</tr>

<script>
  import { _ } from '../../main';

  export let category;
  export let onEdit;
  export let onDelete;
</script>
