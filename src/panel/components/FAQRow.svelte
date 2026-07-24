<tr class:table-active={faq.selected}>
  <!-- Actions Column (Left, Dropdown) -->
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
        <button type="button" class="dropdown-item" on:click={() => onEdit(faq)}>
          <i class="fas fa-pen me-2"></i>
          {$_('faq.edit')}
        </button>
        <button type="button" class="dropdown-item text-danger" on:click={() => onDelete(faq)}>
          <i class="fas fa-trash me-2"></i>
          {$_('faq.delete')}
        </button>
      </div>
    </div>
  </th>

  <!-- ID Column -->
  <td class="align-middle text-center" style="width: 60px;">
    <code>{faq.id}</code>
  </td>

  <!-- Question -->
  <td class="align-middle" style="max-width: 300px;">
    <button
      type="button"
      class="btn btn-link p-0 focus-ring rounded text-decoration-none d-block text-truncate fw-bold w-100 text-start"
      on:click={() => onEdit(faq)}
      title={$_('faq.edit')}>
      {faq.question}
    </button>
  </td>

  <!-- Category -->
  <td class="align-middle text-nowrap">
    <span class="badge {faq.categoryId ? 'text-bg-secondary' : 'text-bg-primary'}">
      {faq.categoryName}
    </span>
  </td>

  <!-- Active Status -->
  <td class="align-middle text-nowrap">
    {#if faq.active}
      <span class="badge text-bg-success">{$_('yes')}</span>
    {:else}
      <span class="badge text-bg-secondary">{$_('no')}</span>
    {/if}
  </td>

  <!-- Display Order -->
  <td class="align-middle text-center">
    {faq.displayOrder}
  </td>
</tr>

<script>
  import { _ } from '../../main';

  export let faq;
  export let onEdit;
  export let onDelete;
</script>
